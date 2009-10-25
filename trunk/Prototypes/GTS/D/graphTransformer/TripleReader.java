package graphTransformer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/* 
 *   TripleReader
 *   Loads a graph from a triple file.
 *
 *   Version description - version 2.8 (Final)
 *   Everything mostly works.
 *   This will not be modified again,
 *   Further updates to this will be in Implementation.
 *   
 *   Date 17/7/09
 *   
 *   Oscar
 *   Contents of Rule
 *
 *     1. loadGraph() 
 *     -description: Reads the file and makes a graph from it.
 *     -inputs: A file, assumed to be of triple format.
 *     -output: The graph that is described in the file.
 *     
 *     2. splitLine()
 *     -description: Internal function for parsing file.
 *     -inputs: A string to split up based on spaces.
 *     -outputs: A list of words in the string.
 *         
 */

public class TripleReader 
{


	public Graph loadGraph(File file) {

		Graph result = new Graph();

		if(!file.getAbsolutePath().contains(".triple")) {
			System.out.println("The input file was not a .triple file.");
		}

		try	{
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			Boolean inComments = false;

			String from = null;
			String type = null;
			String to = null;
	//		Edge edge;

			while ((line = br.readLine()) != null) {		

				ArrayList<String> tokens = splitLine(line);

				for(String str : tokens) {
					if(str.length() > 0 &&
							str.charAt(0) == ' ')
						str = str.substring(1);
					str.replaceAll(" ", "");

					if(str.contains("{*")) {
						inComments = true;
					}
					else if(str.equals("*}")) {
						inComments = false;
						continue;
					}
					else if(str.equals("")) {
						continue;
					}

					if(!inComments) {

						if(from == null) {
							from = str;
						}
						else if(type == null) {
							type = str;
						}
						else if(to == null) {
							// Found the 3 aspects of an edge.
							// Make edge and start again.
							to = str;
							
							/*edge = new Edge(to, type, to, result);
							
							result.add(edge);*/
							
							result.addEdge(to, from, type);
							
							to = null;
							type = null;
							from = null;
						}				
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println("Exception in reading file: " + e);
		}

		return result;

	}

	private static ArrayList<String> splitLine(String line)	{
		Integer lastsplit = 0;
		ArrayList<String> result = new ArrayList<String>();
		for(int n = 0; n < line.length(); n++) {
			if(line.charAt(n) == ' ' || n == line.length()-1) {
				if(n == line.length()-1) {
					result.add(line.substring(lastsplit, n+1));
				}
				else {
					result.add(line.substring(lastsplit, n));
				}
				lastsplit = n;				
			}
		}
		return result;
	}
}
