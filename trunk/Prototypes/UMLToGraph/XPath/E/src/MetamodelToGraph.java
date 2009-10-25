import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import nu.xom.*;

public class MetamodelToGraph {
	private static XPathContext xpathctx;
	
	public static void main(String args[]) throws ValidityException, ParsingException, IOException {
		String outstr = "";			//string for collecting output (to be formatted as dot)
		
		Builder builder = new Builder();
		Document doc = builder.build("UMLMetamodel/08-05-12.xmi");
		xpathctx = XPathContext.makeNamespaceContext(doc.getRootElement());
		Element root = doc.getRootElement();
		
		//Nodes classnodes = root.query("//ownedMember[@xmi:type=\"cmof:Class\"]", xpathctx);
		//Lets strip it out to just the StateMachine elements:
		Nodes classnodes = root.query(
			"//ownedMember[@xmi:id=\"StateMachines\"]//ownedMember[@xmi:type=\"cmof:Class\"]", xpathctx);
		System.out.println("Nodes:");
		for (int i = 0; i < classnodes.size(); i++) {
			System.out.println(classnodes.get(i).query("@name").get(0).getValue() + ", Package = "
				+ classnodes.get(i).query("..").get(0).query("@name").get(0).getValue());
		}
		//NOTE: THE ABOVE CODE PRINTS MULTIPLES OF CLASSES! - eg it finds 5 'Property' nodes
		
		ArrayList<String> nodeList = new ArrayList<String>();
		
		//loop over class nodes
		for (int i = 0; i < classnodes.size(); i++) {
			Node cnode = classnodes.get(i);
			String cname = cnode.query("@name").get(0).getValue();	//the split \|/ is to kill the ns prefix
			String ctype = cnode.query("@xmi:type", xpathctx).get(0).getValue().split(":")[1];
			
			//All nodes get an i edge to their type.
			//To avoid duplicate edges, keep a list of the nodes we find.
			if (!nodeList.contains(cname)) {
				outstr += cname + " i " + ctype + "\n";
				nodeList.add(cname);
			}
			
			//All property elements become edges from parent to type of property, with property name as the label
			Nodes propnodes = cnode.query("ownedAttribute[@xmi:type=\"cmof:Property\"]", xpathctx);
			for (int j = 0; j < propnodes.size(); j++) {
				Nodes typenode = propnodes.get(j).query("@type");
				if (typenode.size() == 0) continue;		//some nodes do not have a type (eg boolean isAbstract)
				String types[] = typenode.get(0).getValue().split("-");
				outstr += cnode.query("@name").get(0).getValue() + " "				//parent node name
					+ propnodes.get(j).query("@name").get(0).getValue() + " "		//name of the property
					+ types[types.length-1] + "\n";									//type of the property
			}
			
			//All superClass attributes become superClass edges from the class to the superClass class 
			Nodes superclassnodes = cnode.query("@superClass");
			if (superclassnodes.size()==1) {	//some nodes do not have a superClass
				//some classes have multiple superclasses, separated by spaces
				String[] superclasses = superclassnodes.get(0).getValue().split(" ");
				for (int j = 0; j < superclasses.length; j++) {
					outstr += cname + " superClass "
						+ superclasses[j].substring(superclasses[j].lastIndexOf("-")+1) + "\n";
				}
			}
		}
		
		System.out.println(outstr);
		outputdot(outstr);
		
		System.out.println();
		
		//Now get some summary data about what elements are in the metamodel
		calcMetamodelSummary(root);
		for (String type : counts.keySet()) {
			System.out.println(type.split(":")[1] + ": " + counts.get(type));
		}
	}
	
	//(eg <Class, 724>, <Property, 113>, etc)
	static HashMap<String,Integer> counts = new HashMap<String,Integer>();
	private static void calcMetamodelSummary(Node node) {
		Nodes types = node.query("@xmi:type", xpathctx);
		if (types.size()>0) {
			if (counts.containsKey(types.get(0).getValue())) {
				counts.put(types.get(0).getValue(), counts.get(types.get(0).getValue())+1);
			} else {
				counts.put(types.get(0).getValue(), 1);
			}
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			calcMetamodelSummary(node.getChild(i));
		}
	}
	
	private static void outputdot(String inputstr) {
		String res = "digraph umlMetamodel {\n";
		//input string will be lines, words[0]=LHS node, words[1]=edge, words[2]=RHS node
		String lines[] = inputstr.split("\n");
		String words[];
		for (int i = 0; i < lines.length; i++) {
			words = lines[i].split(" ");
			if (words.length!=3) {
				System.err.println("Error: This string does not have 3 words: \"" + lines[i] + "\"");
				System.exit(0);
			}
			res += "\t\"" + words[0] + "\" -> \"" + words[2] +
				"\" [label=\"" + words[1] + (words[1].equals("i")?"\", color=\"grey\"":"\"") + "]\n";
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
}