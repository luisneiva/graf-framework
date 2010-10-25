package model.modelTransformer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import model.ListGraph;
import model.exceptions.ModelToGraphException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import agg.xt_basis.Arc;

/**
 * @deprecated mostly
 * 
 * Originally we had UMLToGraph and XMIToGraph to translate 2 different filetypes
 * into graphs for GRAF.
 * 
 * But since XMIToGraph worked fine we kept using that and didn't update UMLToGraph
 * with the newest features. So UMLToGraph now doesn't work on multiple states for 
 * 1 object and possibly other things
 * 
 * If necessary, this could be brought up to date and dedeprecated
 * 
 * 
 * 
 * Transforms an EMF model into a graph. It is currently set up to read UML2 models.
 * 
 * @author Kevin O'Shea
 */
public class UMLToGraph implements ModelToGraph {
	
	/** The graph being built */
	private ListGraph graph;
	
	/** Path of the model file being transformed */
	private String modelPath;
	
	public ListGraph buildGraph(URL modelURL, URL instanceURL) throws ModelToGraphException {
		graph = new ListGraph();

		translateModelPart(modelURL);
		translateInstancePart(instanceURL);
		createRuntimePart();
		return graph;
	}
	
	private void translateModelPart(URL modelURL) throws ModelToGraphException {
		modelPath = modelURL.getPath();
		
		ResourceSet resourceSet = new ResourceSetImpl();
		
		// Register UML package
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		
		Resource modelResource = null; 
		try {
			modelResource = resourceSet.getResource(URI.createFileURI(modelPath),true);
		} catch (RuntimeException e) {
			throw new ModelToGraphException("Could not read model file: " + modelPath);
		}
		EObject documentRoot = modelResource.getContents().get(0);
		
		for (EObject obj : documentRoot.eContents()) {
			buildedgetree(obj);
		}
	}
	
	/** Returns a string suitable for the node in the graph */
	private String getNodeID(EObject obj) {
		String res = "";
		EStructuralFeature valueFeature = obj.eClass().getEStructuralFeature("value");
		EStructuralFeature nameFeature = obj.eClass().getEStructuralFeature("name");
		if (valueFeature != null && obj.eIsSet(valueFeature)) {
			res = obj.eGet(valueFeature).toString();
		} else if (nameFeature != null && obj.eIsSet(nameFeature)) {
			res = obj.eGet(nameFeature).toString();
		} else {
			res = EcoreUtil.getIdentification(obj);
			//try to trim to make graph simpler:
			int hashindex = res.lastIndexOf("#");
			int endindex = res.lastIndexOf("}");
			if (hashindex != -1 && endindex != -1 && hashindex < endindex) {
				res = res.substring(hashindex+1, endindex);
			}
		}
		return res;
	}
	
	private String activityName = null;
	
	private void buildedgetree(EObject obj) throws ModelToGraphException {
		String objType = obj.eClass().getName();
		String nodeName = getNodeID(obj);
		
		//ignore element import nodes:
		if (objType.equals("ElementImport")) return;
		
		//create i edge to the object's type
		graph.addIEdge(nodeName, objType);
		if (objType.equals("AnyType")) {
			throw new ModelToGraphException("The model file "+modelPath+" uses a version of UML2" +
					" that is not supported by your environment. Consider updating this model file.");
		}
		
		//Create an 'activity' edge back to the host activity of any action that
		//doesn't already have this reference.
		if (objType.equals("Activity")) {
			activityName = nodeName; 
		} else if (objType.matches("^.*Action$")) {
			EStructuralFeature activity = obj.eClass().getEStructuralFeature("activity");
			if (activity != null && obj.eGet(activity) == null) {
				graph.addEdge(nodeName, "activity", activityName);
			}
		}
		
		EList<EReference> features = obj.eClass().getEAllReferences();
		for (EStructuralFeature feature : features) {
			if (obj.eIsSet(feature)) {
				Object value = obj.eGet(feature);
				createEdges(obj, feature, value);
			}
		}
		for (EObject childobj : obj.eContents()) {
			buildedgetree(childobj);
		}
	}
	
	private void createEdges(EObject obj, EStructuralFeature feature, Object value) {
		//ignore redundant edges: (not used in current rule definitions)
		String featureName = feature.getName();
		if (featureName.equals("qualifiedName")) return;
		if (featureName.equals("namespace")) return;
		if (featureName.equals("package")) return;
		if (featureName.equals("redefinitionContext")) return;
		if (featureName.equals("owner")) return;
		if (featureName.equals("ownedElement")) return;
		if (featureName.equals("ownedMember")) return;
		if (featureName.equals("member")) return;
		
		if (value instanceof EObject) {
			graph.addEdge(getNodeID(obj), feature.getName(), getNodeID((EObject)value)); 
		} else if (value instanceof EList) {
			for (Object subobj : (EList)value) {
				createEdges(obj, feature, subobj);
			}
		} else {
			graph.addEdge(getNodeID(obj), feature.getName(), value.toString());
		}
	}
	
	private void translateInstancePart(URL instanceURL) throws ModelToGraphException {
		//for keeping track of the object (and class) that owns following state/attributes
		String objName = null;
		String className = null;
		Boolean stateDeclared = true;
		
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(instanceURL.openStream()));
			
			//ignore first non-empty line (defines model file)
			String line = "";
			while((line=bf.readLine())!=null && line.trim().equals("")) {
			}
			while((line = bf.readLine()) != null) {
				line = line.trim();
				
				//ignore white-space lines
				if (line.equals("")) continue;
				
				if (line.matches("^.*:.*$")) {				// class declaration - eg m1 : Microwave
					
					if (stateDeclared == false) {
						instanceErr("No state declared for " + objName + " : " + className);
					}
					stateDeclared = false;
					
					String[] linesplit = line.split(":");
					//remember the object and class names for following lines
					objName = linesplit[0].trim();
					className = linesplit[1].trim();
					graph.addIEdge(objName, className);
					
				} else if (line.matches("^\\{.*\\}$")){		//state declaration - eg {current_state}
					if (stateDeclared) {
						instanceErr("Duplicate state declaration for class "+className);
					}
					
					String state = line.substring(1,line.length()-1).trim();
					String instBehavExecNode = objName + "classifierBehaviorExecution";
					graph.addIEdge(instBehavExecNode, "BehaviorExecution");
					graph.addEdge(objName, "execution", instBehavExecNode);
					graph.addEdge(instBehavExecNode, "host", objName);
					
					// Add behaviour edge from the behavior execution instance to the model statemachine
					//find the statemachine node in the model: obj--i-->classname--classifierBehavior-->statemachine
					//and add an edge: objClassifierBehaviorExecution--behavior-->statemachine
					Boolean statemachinefound = false;
					for (int i = 0; i < graph.getArcsCount(); i++) {
						Arc edge = graph.getArcsList().get(i);
						if (ListGraph.getName(edge.getSource()).equals(className) &&
								ListGraph.getName(edge).equals("classifierBehavior")) {
							statemachinefound = true;
							graph.addEdge(instBehavExecNode, "behavior", ListGraph.getName(edge.getTarget()));
						}
					}
					if (!statemachinefound) {
						instanceErr("ERROR - no state machine found for class " + className);
					}
					stateDeclared = true;
					
					graph.addEdge(instBehavExecNode, "activeState", state);
					
				//attribute value specification - eg temperature = 5
				} else if (line.matches("^.*=.*$")) {
					String[] linesplit = line.split("=");
					String attrname = linesplit[0].trim();
					String attrval = linesplit[1].trim();
					graph.addEdge(objName, attrname, attrval);
					
				//link declaration - eg m1 <cookedBy,cooks> f1
				} else if (line.matches("^.*<.*,.*>.*$")) {
					String[] linesplit = line.split("<");
					String leftObjName = linesplit[0].trim();
					linesplit = linesplit[1].split(">");
					String rightObjName = linesplit[1].trim();
					linesplit = linesplit[0].split(",");
					String leftLinkLabel = linesplit[0].trim();
					String rightLinkLabel = linesplit[1].trim();
					graph.addEdge(leftObjName, rightLinkLabel, rightObjName);
					graph.addEdge(rightObjName, leftLinkLabel, leftObjName);
					
				//unrecognised
				} else {
					instanceErr("Unrecognised line: " + line);
				}
			}
		} catch (FileNotFoundException e) {
			instanceErr("Could not find file " + instanceURL.getPath());
		} catch (IOException e) {
			instanceErr("Error reading file.");
		}
	}
	
	/** Create the edges needed for the runtime part of the graph */
	private void createRuntimePart() {
		String actorname = "actor1";
		graph.addIEdge(actorname, "Actor");
		graph.addIEdge(actorname+"behaviorExecution", "BehaviorExecution");
		graph.addEdge(actorname, "execution", actorname+"behaviorExecution");
		graph.addEdge(actorname+"behaviorExecution", "host", actorname);
	}
	
	/** Performs response when an error occurs during instance translation */
	private void instanceErr(String msg) throws ModelToGraphException {
		throw new ModelToGraphException("Invalid instance file. " + msg);
	}
}
