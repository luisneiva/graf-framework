import java.io.*;
import java.util.ArrayList;

public class TripleToGXL {
	
	public static void main(String args[]) throws IOException {
		String graphName = "state0";
		String output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<gxl xmlns=\"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n" + 
						"\t<graph id=\"" + graphName + "\" role=\"graph\" edgeids=\"false\" edgemode=\"directed\">\n" +
						"\t\t<attr name=\"$version\">\n" +
            			"\t\t\t<string>curly</string>\n" +
            			"\t\t</attr>\n";
		BufferedReader input = new BufferedReader(new FileReader("dot_files/" + graphName + ".dot"));
		input.readLine();
		Integer nodeId = 1;
		String line;
		ArrayList<String> nodeNameList = new ArrayList<String>();
		while ((line = input.readLine()) != null) {
			if (((line.indexOf("->")) == -1)&&((line.indexOf("}")) == -1)) {
				if (line.substring(line.indexOf("[")).compareTo("[  style = invis  ]") < 0) {
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
			else if (line.indexOf("}") == -1) {
				if (line.substring(line.indexOf("]")-15).compareTo("style = invis  ]") < 0) {
					String from = line.substring(1,line.indexOf("->")-2);
					String to = line.substring(line.indexOf("->")+4,line.indexOf("[")-2);
					String endPart = line.substring(line.indexOf("=")+3);
					String label = endPart.substring(0,endPart.indexOf("\""));
					Integer fromNode = 0;
					Integer toNode = 0;
					//System.out.println(from + " " + label + " " + to + " ");
					for (Integer i=0; i < nodeNameList.size(); i++) {
						if (nodeNameList.get(i).equals(from)) {
							fromNode = i+1;
						}
						if (nodeNameList.get(i).equals(to)) {
							toNode = i+1;
						}
					}
					if ((fromNode != 0)&&(toNode != 0)) {
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
		output +=	"\t</graph>\n" + 
					"</gxl>\n";
		
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
