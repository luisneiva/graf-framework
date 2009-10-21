package tests;

import java.net.URL;

import model.Edge;
import model.Graph;
import model.modelTransformer.UMLToGraph;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test class for the UMLToGraph class
 * 
 * @author Kevin
 */
public class UMLToGraphTest {

	String modelPath = "Microwave.uml";
	String instancePath = "microwaveInst.txt";
	
	Graph graph;
	Graph testGraph;
	
	@Before
	public void setup() throws Exception {
		URL modelURL = new URL("file:" + modelPath);
		URL instanceURL = new URL("file:" + instancePath);
		graph = new UMLToGraph().buildGraph(modelURL, instanceURL);
		
		//some basic edges to test for:
		testGraph = new Graph();
		testGraph.addIEdge("Microwave", "Class");
		testGraph.addIEdge("Food", "Class");
		testGraph.addEdge("m1", "cooks", "f1");
		testGraph.addEdge("f1", "cookedBy", "m1");
		testGraph.addEdge("m1", "execution", "m1classifierBehaviorExecution");
		testGraph.addEdge("b1", "execution", "b1classifierBehaviorExecution");
	}
	
	@Test
	public void testBuildGraph() throws Exception {
		for (Edge testEdge : testGraph) {
			Boolean foundMatch = false;
			for (Edge edge : graph) {
				if (testEdge.equals(edge)) {
					foundMatch = true;
					break;
				}
			}
			assertTrue("Could not find a match for " + testEdge, foundMatch);
		}
	}
}