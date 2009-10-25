import java.io.*;
import nu.xom.*;

public class ModelToGraph {
	private static XPathContext xpathctx;
	private static String output = "digraph umlMetamodel {\n";
	
	public static void main(String args[]) throws ValidityException, ParsingException, IOException {
		Builder builder = new Builder();
		Document doc = builder.build("microwaveUMLDiagram.uml");
		xpathctx = XPathContext.makeNamespaceContext(doc.getRootElement());
		Element root = doc.getRootElement();
		
		Nodes classnodes = root.query("//packagedElement[@xmi:type=\"uml:Class\"]", xpathctx);
		System.out.println("Class Nodes:");
		for (int i = 0; i < classnodes.size(); i++) {
			System.out.println(classnodes.get(i).query("@name").get(0).getValue() + " " 
					+ "i Class "
					);
			//outputdot(classnodes.get(i).query("@name").get(0).getValue(), "Class", "i");
		}
		
		System.out.println("\nOther:");
		for (int index = 0; index < classnodes.size(); index++) {
			getChildren(classnodes.get(index));
		}
		
		System.out.println("\nReceiveSignalEvent:");
		Nodes recieveSignalNodes = root.query("//packagedElement[@xmi:type=\"uml:ReceiveSignalEvent\"]", xpathctx);
		for (int i = 0; i < recieveSignalNodes.size(); i++) {
			System.out.println(recieveSignalNodes.get(i).query("@name").get(0).getValue() + " " 
					+ "i ReceiveSignalEvent"
					);
			//outputdot(recieveSignalNodes.get(i).query("@name").get(0).getValue(), 
			//		"ReceiveSignalEvent", "i");
		}
		
		System.out.println("\nSignal:");
		Nodes SignalNodes = root.query("//packagedElement[@xmi:type=\"uml:Signal\"]", xpathctx);
		for (int i = 0; i < SignalNodes.size(); i++) {
			System.out.println(SignalNodes.get(i).query("@name").get(0).getValue() + " " 
					+ "i Signal"
					);
			//outputdot(SignalNodes.get(i).query("@name").get(0).getValue(), 
			//		"Signal", "i");
		}
		output += "}\n";
		try {
			FileWriter fstream = new FileWriter("micro-graph.dot");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(output);
			out.close();
		} catch (Exception e) {
			System.err.println("Error writing file");
		}
	}
	
	public static void getChildren(Node parent) {
		Nodes nodes = new Nodes();
		String childType;
		nodes = parent.query("*", xpathctx);
		for (int j = 0; j < nodes.size(); j++) {
			childType = nodes.get(j).toString();
			if (nodes.get(j).query("@name").size() != 0) {
				System.out.println(parent.query("@name").get(0).getValue() + " "
					+ childType.substring(17,childType.length()-1) + " " 							
					+ nodes.get(j).query("@name").get(0).getValue()
					);
				outputdot(parent.query("@name").get(0).getValue(), 
						nodes.get(j).query("@name").get(0).getValue(),
						childType.substring(17,childType.length()-1));
				if (nodes.get(j).getChildCount() > 0) {
					getChildren(nodes.get(j));
				}
			}
		}
	}
	
	private static void outputdot(String from, String to, String label) {
		output += "\t\"" + from + "\" -> \"" + to + "\" [label=\"" + label + "\"]\n";
	}
}