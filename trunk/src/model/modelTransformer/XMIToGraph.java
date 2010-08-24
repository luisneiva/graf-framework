package model.modelTransformer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import model.ListGraph;
import model.exceptions.ModelToGraphException;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import agg.xt_basis.Arc;
import controller.Activator;

/**
 * Transforms a UML model in XMI form into a graph.
 * 
 * @author Kevin O'Shea
 * @author Frank Su
 */
public class XMIToGraph implements ModelToGraph {

	private final String metamodelpath = "UMLMetamodel/08-05-12.xmi";
	private static Element metamodelroot;
	private static XPathContext xpathctx;

	/** The graph being built */
	private ListGraph graph;

	private HashMap<String,Nodes> metamodelHashmap = new HashMap<String,Nodes>(); 

	public XMIToGraph() {
	}

	public ListGraph buildGraph(URL modelURL, URL instanceURL) throws ModelToGraphException {
		try {
			URL metamodelURL;
			if (Activator.getDefault()==null) {
				metamodelURL = new URL("file:" + metamodelpath); 
			} else {
				metamodelURL = FileLocator.find(Activator.getDefault().getBundle(),
						new Path(metamodelpath), null); 
			}
			Builder metamodelbuilder = new Builder();
			Document metamodeldoc = metamodelbuilder.build(metamodelURL.openStream());
			metamodelroot = metamodeldoc.getRootElement();
			graph = new ListGraph();
			translateModelPart(modelURL);

			translateInstancePart(instanceURL);
			createRuntimePart();
			return graph;
		} catch (Exception e) {
			//e.printStackTrace();
			throw new ModelToGraphException(e.getMessage());
		}
	}

	private void translateModelPart(URL modelURL) throws ValidityException, ParsingException, IOException {
		Builder builder = new Builder();
		Document doc = builder.build(modelURL.openStream());
		xpathctx = XPathContext.makeNamespaceContext(doc.getRootElement());
		Element root = doc.getRootElement();

		Elements children = root.getChildElements();
		for (int i = 0; i < children.size(); i++) {
			buildedgetree(children.get(i));
		}
	}

	// Build all of the edges:
	// - Every node with a type will get an i edge to the name of its type
	// - Every node will have an edge going from it to its child elements, where the label is the <elementname>
	//   of the element.
	// - Every node will have an edge going from it to its attribute values, where the label is the attribute name. 
	// - The node names will be the "name" attribute of the nodes, OR if they don't have a name then it will
	//   be the <localname> concatenated with the last 4 letters of their xmi:id.
	private void buildedgetree(Element root) {
		String rootname = graphNodeName(root);

		//if the name has no way to identify it (no name, value, href, or xmi:id), then ignore it. 
		if (rootname == null) {
			return;
		}
		//if the node is an elementImport, then ignore it.
		if (root.getLocalName().equals("elementImport")) {
			return;
		}

		String identifierAttr = null;
		if (root.query("@value").size()==1) identifierAttr = "value";
		else if (root.query("@name").size()==1) identifierAttr = "name";
		else if (root.query("@href").size()==1) identifierAttr = "href";
		else if (root.query("@xmi:id",xpathctx).size()==1) identifierAttr = "xmi:id";

		//if the node does not have an xmi:type (specialization) then must look up
		//its (general) type in the metamodel
		if (root.query("@xmi:type",xpathctx).size()==0) {
			Nodes typenodes = null;
			if (metamodelHashmap.containsKey(root.getLocalName())) {
				typenodes = metamodelHashmap.get(root.getLocalName());
			} else {
				typenodes = metamodelroot.query("//*[@name=\"" + root.getLocalName() + "\"]/@type");
				metamodelHashmap.put(root.getLocalName(), typenodes);
			}
			if (typenodes.size()>0) {
				String typename = typenodes.get(0).getValue();
				//strip context stuff
				String[] typenames = typename.split("-");
				typename = typenames[typenames.length-1];
				graph.addIEdge(rootname, typename);
			}
		}

		// make edges from the element's attributes
		Nodes attrnodes = root.query("@*");
		for (int i = 0; i < attrnodes.size(); i++) {
			String attrname = attrnodes.get(i).toXML().split("=")[0];
			String attrval = attrnodes.get(i).getValue();

			//ignore the xmi:id's - they don't need to be made into edges
			if (attrname.equals("xmi:id")) continue;
			//ignore the attribute that is used as the name of the node (redundant reflexive edge)
			if (attrname.equals(identifierAttr)) continue;

			//xmi:type indicates instantiation so make it an 'i' edge
			if (attrname.equals("xmi:type")) attrname = "i";

			//kill namespace prefixes:
			if (attrname.contains(":")) attrname = attrname.split(":")[1];
			if (attrval.contains(":")) attrval = attrval.split(":")[1];

			//memberEnd attributes contain space-seperated entries - seperate into seperate nodes
			if (attrname.equals("memberEnd")) {
				if (attrval.contains(" ")) {
					String[] attrvals = attrval.split(" ");
					for (int j = 0; j < attrvals.length; j++) {
						attrnodes.append(new Attribute("memberEnd", attrvals[j]));
					}
					continue;
				}

			}

			//For any action, create an 'activity' edge back to its host activity
			if (attrname.equals("i") && attrval.equals("Activity")) {
				activityName = rootname;
			}
			if (attrname.equals("i") && attrval.matches("^.*Action$")) {
				graph.addEdge(rootname, "activity", activityName);
			}

			//some attribute values are xmi:id (_*) references to other objects - convert the name
			if (attrval.startsWith("_") || attrval.startsWith("id")) {
				Nodes idnodes = root.query("//@xmi:id", xpathctx);
				for (int j = 0; j < idnodes.size(); j++) {
					if (idnodes.get(j).getValue().equals(attrval)) {
						attrval = graphNodeName((Element)idnodes.get(j).query("..").get(0));
					}
				}
			}

			graph.addEdge(rootname, attrname, attrval);
		}

		// make edges from the element's children
		Elements children = root.getChildElements();
		for (int i = 0; i < children.size(); i++) {
			Element child = children.get(i);
			String childname = graphNodeName(child);
			graph.addEdge(rootname, child.getLocalName(), childname);

			buildedgetree(child);
		}
	}
	private String activityName;

	private void translateInstancePart(URL instanceURL) throws Exception {
		//for keeping track of the object (and class) that owns following state/attributes
		String objName = null;
		String className = null;
		Boolean stateDeclared = true;
		String[] states;


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

					//					if (stateDeclared == false) {
					//						instanceErr("No state declared for " + objName + " : " + className);
					//					}
					stateDeclared = false;

					String[] linesplit = line.split(":");
					//remember the object and class names for following lines
					objName = linesplit[0].trim();
					className = linesplit[1].trim();
					graph.addIEdge(objName, className);

					//add in initial details: default attribute values and default initial state


				} else if (line.matches("^\\{.*\\}$")){		//state declaration - eg {current_state}
					states = line.substring(1,line.length()-1).trim().split(";");
					
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

					// Add activeState edge from the behaviour execution instance to the indicated model state

					for(int j = 0; j < states.length; j++)
					{
						graph.addEdge(instBehavExecNode, "activeState", states[j]);
					}
			} else if (line.matches("^.*=.*$")) {		//attribute value specification - eg temperature = 5
				String[] linesplit = line.split("=");
				String attrname = linesplit[0].trim();
				String attrval = linesplit[1].trim();
				graph.addEdge(objName, attrname, attrval);
			} else if (line.matches("^.*<.*,.*>.*$")) { //link declaration - eg m1 <cookedBy,cooks> f1
				String[] linesplit = line.split("<");
				String leftObjName = linesplit[0].trim();
				linesplit = linesplit[1].split(">");
				String rightObjName = linesplit[1].trim();
				linesplit = linesplit[0].split(",");
				String leftLinkLabel = linesplit[0].trim();
				String rightLinkLabel = linesplit[1].trim();
				graph.addEdge(leftObjName, rightLinkLabel, rightObjName);
				graph.addEdge(rightObjName, leftLinkLabel, leftObjName);
			} else {
				instanceErr("Unrecognised line: " + line);
			}
		}
		//			if (stateDeclared == false) {
		//				instanceErr("No state declaration for object " + objName + " : " + className);
		//			}
	} catch (FileNotFoundException e) {
		instanceErr("Could not find file " + instanceURL.getPath());
	}
}

/**
 * Extract a string that represents this node and is suitable for a node name in the graph.
 * Nodes are identified by their name, value, href, or xmi:id (in this order of preference)
 * If a node does not contain any of these identifying elements then this method returns null.
 */
//eg:
//<ownedAttribute xmi:id="_Ih6EMAcQEd6hmNr2e6F5xg" name="radius" aggregation="composite">
//  <type xmi:type="uml:PrimitiveType" href="pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#Integer"/>
//  <defaultValue xmi:type="uml:LiteralString" xmi:id="_aFInAAlYEd6Zz8oMV4pXtg" name="" value="10"/>
//</ownedAttribute>
private static String graphNodeName(Element node) {
	Nodes namenodes = node.query("@name");
	Nodes typenodes = node.query("@xmi:type",xpathctx);
	Nodes valuenodes = node.query("@value");
	Nodes hrefnodes = node.query("@href");
	Nodes idnodes = node.query("@xmi:id",xpathctx);
	String name = "";
	if (valuenodes.size()==1)
		name = valuenodes.get(0).getValue();
	else if (namenodes.size()==1 && !namenodes.get(0).getValue().equals(""))
		name = namenodes.get(0).getValue();
	else if (hrefnodes.size()==1)
		name = hrefnodes.get(0).getValue().split("#")[1];	
	else if (idnodes.size()==1) {
		//concatenate the first 4 letters of its xmi:id at the end of the element's type,
		//or if it doesn't have a type then use its <elementname>
		String prefix;
		String idstr = idnodes.get(0).getValue();
		if (typenodes.size()==1) {
			prefix = typenodes.get(0).getValue().split(":")[1];
		} else {
			prefix = node.getLocalName();
		}
		name = prefix + idstr.substring(0,4);
	} else {	//if no name, no value, and no xmi:id (eg <body> descriptions)
		return null;
	}
	return name;
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
