
import java.io.*;

// This program reads in a GROOVE .gpr (rule) file and then creates a more readable rule format
// to be used in the GTSRules.xml file.

public class GPR_To_Rule {

	public static void main(String args[]) throws IOException {
		//sets up the initial output that the file should have
		String output = "";
		// sets up the .gpr file for reading. 
		String graphName = "rSFA_BEFORE";
		BufferedReader input = new BufferedReader(new FileReader("rules_files/" + graphName + ".gpr"));
		
		// this skips the first 10 lines seeing as they are not needed
		for (int index=0; index < 10; index++) {
			input.readLine();
		}
		
		// this is the part that reads in the old format and produces a new format
		String line;
		while ((line = input.readLine()) != null) {
			if (line.indexOf("</graph>") == -1) {
				if (line.indexOf("edge") == -1) {
					// The line is not an edge and is therefore not needed.
				}
				else {
					System.out.print(line.substring(0,line.indexOf(">")) + " label=\"");
					output += line.substring(0,line.indexOf(">")) + " label=\"";
					input.readLine();
					line = input.readLine();
					System.out.print(line.substring(line.indexOf(">")+1,line.indexOf("</")) + "\"/>\n");
					output += line.substring(line.indexOf(">")+1,line.indexOf("</")) + "\"/>\n";
					input.readLine();
					input.readLine();
				}
			}
			else {
				System.out.println("Reached End Of File");
			}
		}
		
		// this is the part that writes the output out to a file.
		try {
			FileWriter fstream = new FileWriter(graphName + ".rule");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(output);
			out.close();
		} catch (Exception e) {
			System.err.println("Error writing file");
		}
	}

}
