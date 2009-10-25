import java.io.*;
import java.util.ArrayList;

//This program reads in a .dot file and then creates a .gst file that GROOVE uses as its graph
//This is so that we can check the rules against the graph.

//NOTE: to get this to work sometimes you need to make sure that no tabs are used in the file.

public class DotToGXL {
	
	public static void main(String args[]) throws IOException {		
		// sets up the .dot file for reading.
		String graphName = "state0";
		BufferedReader input = new BufferedReader(new FileReader("dot_files/" + graphName + ".dot"));
		
		// sets up the initial output that the file should have
		String output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<gxl xmlns=\"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n" + 
						"\t<graph id=\"" + graphName + "\" role=\"graph\" edgeids=\"false\" edgemode=\"directed\">\n" +
						"\t\t<attr name=\"$version\">\n" +
            			"\t\t\t<string>curly</string>\n" +
            			"\t\t</attr>\n";
		
		// skips the first line of the file that is not needed.
		input.readLine();
		
		// sets up some of the variables to be used
		Integer nodeId = 1;
		String line;
		ArrayList<String> nodeNameList = new ArrayList<String>();
		
		// this reads the entire file and extracts the parts that are needed.
		while ((line = input.readLine()) != null) {
			// this test if it has reached the } at the end of the file or and edge.
			if (((line.indexOf("->")) == -1)&&((line.indexOf("}")) == -1)) {
				// test if the node is suppose to be invisible
				if (line.substring(line.indexOf("[")).compareTo("[  style = invis  ]") < 0) {
					// this creates a node
					Integer nodeNameStartIndex = line.indexOf("\"")+1;
					Integer nodeNameEndIndex = line.substring(nodeNameStartIndex).indexOf("\"")+1;
					String nodeName = line.substring(nodeNameStartIndex,nodeNameEndIndex);
					nodeNameList.add(nodeName);
					output += "\t\t<node id=\"n" + nodeId + "\"/>\n" +
								"\t\t<edge from=\"n" + nodeId + "\" to=\"n" + nodeId + "\">\n" +
								"\t\t\t<attr name=\"label\">\n" +
								"\t\t\t\t<string>" + nodeName + "</string>\n" +
								"\t\t\t</attr>\n" +
								"\t\t</edge>\n";
					nodeId++;
				}
			}
			// this test if it has reached the } at the end of the file 
			else if (line.indexOf("}") == -1) {
				if (line.substring(line.indexOf("]")-15).compareTo("style = invis  ]") < 0) {
					String from = line.substring(1,line.indexOf("->")-2);
					String to = line.substring(line.indexOf("->")+4,line.indexOf("[")-2);
					String endPart = line.substring(line.indexOf("=")+3);
					String label = endPart.substring(0,endPart.indexOf("\""));
					// tries to find the from and to nodes that are needed for the edge
					Integer fromNode = 0;
					Integer toNode = 0;
					for (Integer i=0; i < nodeNameList.size(); i++) {
						if (nodeNameList.get(i).equals(from)) {
							fromNode = i+1;
						}
						if (nodeNameList.get(i).equals(to)) {
							toNode = i+1;
						}
					}
					// if both nodes are found then create the edge
					if ((fromNode != 0)&&(toNode != 0)) {
						// creates an edge between the from node and the to node that is labelled label
						output += "\t\t<edge from=\"n" + fromNode + "\" to=\"n" + toNode + "\">\n" +
								"\t\t\t<attr name=\"label\">\n" +
								"\t\t\t\t<string>" + label + "</string>\n" +
								"\t\t\t</attr>\n" +
								"\t\t</edge>\n";
					}
				}
				
			}
			else {
				System.out.println("Reached End Of File");
			}
		}
		
		// appends the following to the end of the file to complete it.
		output +=	"\t</graph>\n" + 
					"</gxl>\n";
		
		// this is the part that writes the output out to a file.
		try {
			FileWriter fstream = new FileWriter(graphName + ".gst");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(output);
			out.close();
		} catch (Exception e) {
			System.err.println("Error writing file");
		}
	}

}