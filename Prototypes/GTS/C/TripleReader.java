/* 
 *   TripleReader
 *   Reads a .triple file and creates a Graph object that is the graph represented in the file.
 *
 *   Version description - version 1.3
 *   	Extracts object diagrams (as strings) from graphs. 
 *   		But this will be extended in prototype G.
 *   	Loads .triple files.
 *   	Tests for subgraphs using tracing in a very limited way.
 *   	Doesn't do much else.
 *   
 *   Date 3/6/09
 *
 *   Oscar
 *   Contents of TripleReader
 *
 *     1. loadGraph()
 *     -description: takes a triple file and turns it into a graph java object.
 *     -inputs: a file, assumed to be in triple format
 *     -output: graph
 *     
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
							to = str;
							
							Node toNode;
							Node fromNode;

							Boolean mustAddFrom = false;
							Boolean mustAddTo = false;

							toNode = result.getNodeWithName(to);
							if(toNode == null) {
								toNode = new Node(to);
								mustAddTo = true;
							}

							fromNode = result.getNodeWithName(from);
							if(fromNode == null) {
								fromNode = new Node(from);
								mustAddFrom = true;
							}

						//	System.out.println(from + " " + type + " " + to + " " + mustAddFrom + " " + mustAddTo);
							
							edge = new Edge(type);
							edge.connect(toNode, fromNode);

							if(mustAddTo) {
								result.add(toNode);
							}
							if(mustAddFrom) {
								result.add(fromNode);
							}

							to = null;
							type = null;
							from = null;
						}				
					}
				}
			}
			System.out.println();
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
