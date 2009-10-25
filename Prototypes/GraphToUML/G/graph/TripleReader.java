package graph;
/* 
 *   TripleReader
 *   This class creates a graph object from a .triple file.
 *
 *   Version description - version 1.6 (Final)
 *   This will not be modified again.
 *   Changes will be made to Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of TripleReader
 *
 *     1. loadGraph()
 *     -description: Translates a triple file into a graph object.
 *     -inputs: A file, assumed to be .triple.
 *     -outputs: A graph.
 */


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


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
			Edge edge;

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
							// Found 3 elements of an edge.
							// Make the edge and start again.
							to = str;
							
							Node fromNode = result.getNodeWithName(from);
							Node toNode = result.getNodeWithName(to);
							
							if(fromNode == null) {
								fromNode = new Node(from);
							}
							if(toNode == null) {
								toNode = new Node(to);
							}
							
							edge = new Edge(fromNode, type, toNode);
							
							result.add(edge);
							
							to = null;
							type = null;
							from = null;
						}				
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println("Exception in reading file: " + e + "\nWas it a .triple file?");
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
