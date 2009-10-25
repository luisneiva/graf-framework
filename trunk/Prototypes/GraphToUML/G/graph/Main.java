package graph;
/* 
 *   Main
 *   Calls methods from other classes, just used for testing.
 *
 *   Version description - version 1.6 (Final)
 *   This will not be modified again.
 *   Changes will be made to Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of Node
 *
 *     1. main()
 *     -description: Tests object diagram drawing.
 *     -inputs: None.
 *     -outputs: None.
 */



import java.io.File;


public class Main {

	public static void main(String[] args) {

		TripleReader reader = new TripleReader();
		Graph microwave = reader.loadGraph(new File("microwave.triple"));
	//	System.out.println(microwave.toString());
		
		System.out.println(microwave.generateObjectDiagram());
	}

}
