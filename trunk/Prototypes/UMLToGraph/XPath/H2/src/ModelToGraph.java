import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import nu.xom.*;

public class ModelToGraph {
	private static Element metamodelroot;
	private static XPathContext metamodelxpathctx;
	private static XPathContext xpathctx;
	
	/** Used in differentiating model edges from instance edges (when colouring edges in dot output) */
	private static Integer numModelElems;
	
	private static HashMap<String,Nodes> metamodelHashmap = new HashMap<String,Nodes>();
	
	private static ArrayList<Edge> edges = new ArrayList<Edge>();
	private static class Edge {
		String from, label, to;
		Edge(String fromstr, String lbl, String tostr) {from=fromstr; label=lbl; to=tostr; }
	}
	
	public static void main(String args[]) throws ValidityException, ParsingException, IOException {
		translateModelPart();
		numModelElems = edges.size();
		translateInstancePart(new URL("file:microwaveInst.txt"));
		for (Edge e : edges) System.out.println(e.from + " " + e.label + " " + e.to);
		outputdot();
	}
	
	private static void translateModelPart() throws ValidityException, ParsingException, IOException {
		Builder builder = new Builder();
		//Document doc = builder.build("../../EMF/A/myUMLDiagram.uml");
		Document doc = builder.build("microwaveUMLDiagram.uml");
		xpathctx = XPathContext.makeNamespaceContext(doc.getRootElement());
		Element root = doc.getRootElement();
		
		Builder metamodelbuilder = new Builder();
		Document metamodeldoc = metamodelbuilder.build("../E/UMLMetamodel/08-05-12.xmi");
		metamodelxpathctx = XPathContext.makeNamespaceContext(metamodeldoc.getRootElement());
		metamodelroot = metamodeldoc.getRootElement();
		
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
	private static void buildedgetree(Element root) {
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
				System.out.println(root.getLocalName() + " => " + typenodes.get(0).getValue());
			}
			if (typenodes.size()==1) {
				String typename = typenodes.get(0).getValue();
				//strip context stuff
				String[] typenames = typename.split("-");
				typename = typenames[typenames.length-1];
				edges.add(new Edge(rootname, "i", typename));
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
				
			//some attribute values are xmi:id (_*) references to other objects - convert the name
			if (attrval.startsWith("_")) {
				Nodes idnodes = root.query("//@xmi:id", xpathctx);
				for (int j = 0; j < idnodes.size(); j++) {
					if (idnodes.get(j).getValue().equals(attrval)) {
						attrval = graphNodeName((Element)idnodes.get(j).query("..").get(0));
					}
				}
			}
						
			edges.add(new Edge(rootname, attrname, attrval));
		}
		
		// make edges from the element's children
		Elements children = root.getChildElements();
		for (int i = 0; i < children.size(); i++) {
			Element child = children.get(i);
			String childname = graphNodeName(child);
			edges.add(new Edge(rootname, child.getLocalName(), childname));
			
			buildedgetree(child);
		}
	}
	
	private static void translateInstancePart(URL instanceURL) throws ValidityException, ParsingException, IOException {
		//for keeping track of the object (and class) that owns following state/attributes
		String objName = null;
		String className = null;
		Boolean stateDeclared = true;
		
		/*////////////////////////////////////////TODO:
		 * - Check that no double-declarations are made (eg no 2 state {} in the same class declaration)
		 * - Check that all classnames being instantiated are present in the model.
		 * - Make sure all object names are unique - eg can't have 'm1 : Microwave' and 'm1 : Food' (or do something to avoid clashes)
		 * ///////////////////////////////////////////////////
		 */
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(instanceURL.openStream()));
			
			String line;
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
					edges.add(new Edge(objName, "i", className));
					
					//add in initial details: default attribute values and default initial state
					
					
				} else if (line.matches("^\\{.*\\}$")){		//state declaration - eg {current_state}
					String state = line.substring(1,line.length()-1).trim();
					////////////////////TODO: this should be done at the model level or some runtime level????
					String instBehavExecNode = objName + "classifierBehaviorExecution";
					edges.add(new Edge(instBehavExecNode, "i", "BehaviorExecution"));
					edges.add(new Edge(objName, "execution", instBehavExecNode));
					edges.add(new Edge(instBehavExecNode, "host", objName));
					////////////////////
					//(the below stuff \|/ may also be runtime-creation-stuff)
					// Add behaviour edge from the behavior execution instance to the model statemachine
					//find the statemachine node in the model: obj--i-->classname--classifierBehavior-->statemachine
					//and add an edge: objClassifierBehaviorExecution--behavior-->statemachine
					Boolean statemachinefound = false;
					for (int i = 0; i < edges.size(); i++) {
						Edge edge = edges.get(i);
						if (edge.from.equals(className) && edge.label.equals("classifierBehavior")) {
							statemachinefound = true;
							edges.add(new Edge(instBehavExecNode, "behavior", edge.to));
						}
					}
					if (!statemachinefound) System.err.println("ERROR - no state machine found for class " + className);
					
					stateDeclared = true;
					
					// Add activeState edge from the behaviour execution instance to the indicated model state
					edges.add(new Edge(instBehavExecNode, "activeState", state));
				} else if (line.matches("^.*=.*$")) {		//attribute value specification - eg temperature = 5
					String[] linesplit = line.split("=");
					String attrname = linesplit[0].trim();
					String attrval = linesplit[1].trim();
					edges.add(new Edge(objName, attrname, attrval));
				} else if (line.matches("^.*<.*,.*>.*$")) { //link declaration - eg m1 <cookedBy,cooks> f1
					String[] linesplit = line.split("<");
					String leftObjName = linesplit[0].trim();
					linesplit = linesplit[1].split(">");
					String rightObjName = linesplit[1].trim();
					linesplit = linesplit[0].split(",");
					String leftLinkLabel = linesplit[0].trim();
					String rightLinkLabel = linesplit[1].trim();
					edges.add(new Edge(leftObjName, rightLinkLabel, rightObjName));
					edges.add(new Edge(rightObjName, leftLinkLabel, leftObjName));
				} else {
					instanceErr("Unrecognised line: " + line);
				}
			}
			if (stateDeclared == false) {
				instanceErr("No state declaration for object " + objName + " : " + className);
			}
			
			//TODO: add in edges that weren't specified - eg missing attribute values => get default values
			//(if no default values then don't add in)
			//(THEN, do validation - if now missing edges then invalid specification)
		} catch (FileNotFoundException e) {
			instanceErr("Could not find file " + instanceURL.getPath());
		}
	}
	
	/**
	 * Extract a string that represents this node and is suitable for a node name in the graph.
	 * Nodes are identified by their name, value, href, or xmi:id (in this order of preference) (<-TODO)
	 * If a node does not contain any of these identifying elements then this method returns null.
	 */
	//eg:
	//<ownedAttribute xmi:id="_Ih6EMAcQEd6hmNr2e6F5xg" name="radius" aggregation="composite">
    //  <type xmi:type="uml:PrimitiveType" href="pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#Integer"/>
    //  <defaultValue xmi:type="uml:LiteralString" xmi:id="_aFInAAlYEd6Zz8oMV4pXtg" name="" value="10"/>
    //</ownedAttribute>
	private static String graphNodeName(Element node) {
		
		//TODO
		//TODO
		//TODO Every name generated must be unique across graph. This means that if
		//TODO name or value is used, then must append the xmi:id to the end of it.
		//TODO
		//TODO
		//TODO:
		// I propose: refactor this - start with String identifierprefix. Then set this prefix
		// to either value, name, or <elementname> (NOT href - maybe move href to highest precedence?)
		// Then get the idstr - either full str, or first 4 characters. (make this easy to change)
		// Then the name will be the identifierprefix + idstr.
		
		Nodes namenodes = node.query("@name");
		Nodes typenodes = node.query("@xmi:type",xpathctx);
		Nodes valuenodes = node.query("@value");
		Nodes hrefnodes = node.query("@href");
		Nodes idnodes = node.query("@xmi:id",xpathctx);
		String name = "";
		if (valuenodes.size()==1)
			name = valuenodes.get(0).getValue();	// \|/ some elements have an empty name which is useless.
		else if (namenodes.size()==1 && !namenodes.get(0).getValue().equals(""))	//TODO - remove this, but only when xmi:id is appended (risk of two things having a blank name)
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
	
	private static void outputdot() {
		String res = "digraph umlMetamodel {\n";
		for (Edge edge : edges) {
			res += "\t\"" + edge.from + "\" -> \"" + edge.to +
				"\" [label=\"" + edge.label + (edge.label.equals("i")?"\", color=\"grey\"":"\"") + "]\n";
		}
		//colour nodes, but work from the bottom up since lower node colours overwrite previous ones
		for (int i = numModelElems; i < edges.size(); i++) {
			res += "\t\"" + edges.get(i).from + "\"" + " [color=\"green\", fontcolor=\"green\"]" + "\n";
			res += "\t\"" + edges.get(i).to + "\"" + " [color=\"green\", fontcolor=\"green\"]" + "\n";
		}
		for (int i = 0; i < numModelElems; i++) {
			res += "\t\"" + edges.get(i).from + "\"" + " [color=\"red\", fontcolor=\"red\"]" + "\n";
			res += "\t\"" + edges.get(i).to + "\"" + " [color=\"red\", fontcolor=\"red\"]" + "\n";
		}
		res += "}\n";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("graph.dot"));
			out.write(res);
			out.close();
			System.out.println("Dot code written");
		} catch (Exception e) {
			System.err.println("Error writing dot file");
		}
	}
	/** Performs response when an error occurs during instance translation */
	private static void instanceErr(String msg) {
		System.err.println("Invalid instance file. " + msg);
	}
}