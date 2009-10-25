package graphTransformer;

import java.io.File;
import java.util.ArrayList;

/* 
 *   GraphTransformerTest
 *   This class simply executes code from other classes.
 *   At the moment the contents of main could be changed to
 *   whatever is needed.
 *
 *   Version description - version 2.8 (Final)
 *   Everything mostly works.
 *   This will not be modified again,
 *   Further updates to this will be in Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of GraphTransformerTest
 *
 *     1. main()
 *     -description: This class currently does nothing in particular.
 *                   It's just used to test the other classes.
 *     -inputs: None.
 *     -outputs: At the moment it just prints information.
 */


public class GraphTransformerTester {


	public static void main(String[] arg) {

		/*TripleReader reader = new TripleReader();
		Graph microwave = reader.loadGraph(new File("microwave.triple"));
		
		System.out.println(microwave.generateObjectDiagram());
*/
		
		long time = System.currentTimeMillis();
		doTests(false);
		System.out.println("Time taken " + (System.currentTimeMillis()-time) + " ns");
	}

	private static void doTests(Boolean printAll) {
		Graph before;
		Graph after;
		Rule rule;
		Integer number = 1;

		System.out.println("----------\nTEST 1: basic add and delete\n----------\n");

		Graph testone = new Graph();
		testone.addEdge("B", "A", "target");
		testone.addEdge("E", "B", "in");
		testone.addIEdge("B", "C");
		testone.addEdge("B", "D", "host");
		testone.addEdge("C","B","in");
		testone.addIEdge("D","C");

		before = new Graph();
		before.addEdge("B", "?A", "target");
		before.addEdge("C", "B", "in");
		before.addIEdge("D", "C");

		after = new Graph();
		after.addEdge("B", "?A", "target");
		after.addEdge("D", "B", "in");

		rule = new Rule(before, after);

		System.out.println(
				test(rule, testone, printAll, 1, number++));

		System.out.println("----------\nTEST 2: match to 1 variable edge\n----------\n");

		Graph test2 = new Graph();
		test2.addIEdge("Class", "Oven");
		test2.addIEdge("Class", "State");
		test2.addIEdge("State", "state1");
		test2.addIEdge("State", "state2");
		test2.addEdge("Oven", "state1", "has");
		test2.addEdge("Oven", "state2", "has");
		test2.addEdge("state2","BehaviorExecution","in");

		before = new Graph();
		before.addIEdge("Class", "?obj");
		before.addIEdge("Class", "State");
		before.addIEdge("State", "state1");
		before.addIEdge("State", "state2");	
		before.addEdge("?obj", "state1", "has");
		before.addEdge("?obj", "state2", "has");
		before.addEdge("state2","BehaviorExecution","in");

		after = new Graph();
		after.addIEdge("Class", "?obj");
		after.addIEdge("Class", "State");
		after.addIEdge("State", "state1");
		after.addIEdge("State", "state2");
		after.addEdge("?obj", "state1", "has");
		after.addEdge("?obj", "state2", "has");
		after.addEdge("state1","BehaviorExecution","in");

		rule = new Rule(before, after);

		System.out.println(
				test(rule, test2, printAll, 1, number++));

		System.out.println("----------\nTEST 3: match to 3 variable edge\n----------\n");

		Graph test3 = new Graph();
		test3.addIEdge("Class", "Oven");
		test3.addIEdge("Class", "State");
		test3.addIEdge("State", "on");
		test3.addIEdge("State", "off");		
		test3.addEdge("Oven", "on", "has");
		test3.addEdge("Oven", "off", "has");
		test3.addEdge("off","BehaviorExecution","in");

		before = new Graph();
		before.addIEdge("Class", "?object");
		before.addIEdge("Class", "State");
		before.addIEdge("State", "?state1");
		before.addIEdge("State", "?state2");		
		before.addEdge("?object", "?state1", "has");
		before.addEdge("?object", "?state2", "has");
		before.addEdge("?state2","BehaviorExecution","in");

		after = new Graph();
		after.addIEdge("Class", "?object");
		after.addIEdge("Class", "State");
		after.addIEdge("State", "?state1");
		after.addIEdge("State", "?state2");		
		after.addEdge("?object", "?state1", "has");
		after.addEdge("?object", "?state2", "has");
		after.addEdge("?state1","BehaviorExecution","in");

		rule = new Rule(before, after);

		System.out.println(
				test(rule, test3, printAll, 1, number++));

		System.out.println("----------\nTEST 4: a more complicated example involving variable nodes, adding and deleting\n----------\n");		

		Graph test4 = new Graph();
		test4.addIEdge("B","A");
		test4.addEdge("F","B", "in");
		test4.addIEdge("H","F");
		test4.addIEdge("F","G");
		test4.addEdge("E","H", "in");
		test4.addEdge("E","G", "in");

		before = new Graph();
		before.addIEdge("B","A");
		before.addEdge("?C","B", "in");
		before.addIEdge("?C","?D");
		before.addEdge("E","?D", "in");

		after = new Graph();
		after.addEdge("A", "J", "in");
		after.addIEdge("B", "A");
		after.addEdge("?C", "B", "in");

		rule = new Rule(before, after);

		System.out.println(
				test(rule, test4, printAll, 1, number++));

		System.out.println("----------\nTEST 5: a graph with 2 possible implementations\n----------\n");		

		Graph test5 = new Graph();
		test5.addIEdge("B","A");
		test5.addIEdge("A","C");
		test5.addIEdge("A","D");

		before = new Graph();
		before.addIEdge("A","?E");
		before.addIEdge("B","A");

		after = new Graph();
		after.addIEdge("B","A");

		rule = new Rule(before, after);

		System.out.println(
				test(rule, test5, printAll, 2, number++));

		System.out.println("----------\nTEST 6: a graph where a double variable edge could fool the subgraph check\n----------\n");		

		Graph g6 = new Graph();
		g6.addIEdge("A","C");

		before = new Graph();
		before.addIEdge("A","C");
		before.addIEdge("A","?B");

		rule = new Rule(before, new Graph());

		System.out.println(
				test(rule, g6, printAll, 0, number++));

		System.out.println("----------\nTEST 7: Wilson's example in client meeting 21/5\n----------\n");		

		Graph g7 = new Graph();
		g7.addIEdge("A", "D");
		g7.addIEdge("C", "A");
		g7.addIEdge("B", "C");

		before = new Graph();
		before.addIEdge("C","A");
		before.addIEdge("?E","C");
		before.addIEdge("A","?E");

		rule = new Rule(before, new Graph());

		System.out.println(
				test(rule, g7, printAll, 0, number++));


		System.out.println("----------\nTEST 8: another tricky homomorphism test\n----------\n");		

		Graph g8 = new Graph();
		g8.addIEdge("B","A");
		g8.addIEdge("E","B");
		g8.addIEdge("C","B");
		g8.addIEdge("D","E");

		before = new Graph();
		before.addIEdge("B","A");
		before.addIEdge("?F","B");
		before.addIEdge("?G","B");
		before.addIEdge("?H","?F");

		rule = new Rule(before, new Graph());

		System.out.println(
				test(rule, g8, printAll, 1, number++));


		System.out.println("----------\nTEST 9: very many variable nodes\n----------\n");		

		Graph g9 = new Graph();
		g9.addIEdge("G","H");
		g9.addIEdge("H","I");
		g9.addIEdge("I","J");
		g9.addIEdge("J","K");
		g9.addIEdge("K","L");
		g9.addIEdge("F","L");

		before = new Graph();
		before.addIEdge("?A","?B");
		before.addIEdge("?B","?C");
		before.addIEdge("?C","?D");
		before.addIEdge("?D","?E");
		before.addIEdge("?E","?M");
		before.addIEdge("F","?M");

		rule = new Rule(before, new Graph());

		System.out.println(
				test(rule, g9, printAll, 1, number++));


		System.out.println("----------\nTEST 10: an edge that must not be in the graph\n----------\n");

		Graph before10 = new Graph();
		before10.addIEdge("?C","B");

		Graph forbidden = new Graph();
		forbidden.addEdge("B","?C","target");

		Graph after10 = new Graph();
		after10.addIEdge("B","A");
		after10.addIEdge("?C","B");

		Graph g10 = new Graph();
		g10.addIEdge("A","B");
		g10.addEdge("B","A","target");

		Rule rule10 = new Rule(before10, after10, forbidden);

		System.out.println(
				test(rule10, g10, printAll, 0, number++));


		System.out.println("----------\nTEST 11: complicated multiple variables\n----------\n");

		Graph g11 = new Graph();

		g11.addIEdge("G","H");
		g11.addIEdge("H","I");
		g11.addIEdge("I","J");
		g11.addIEdge("J","K");
		g11.addIEdge("K","L");
		g11.addIEdge("F","L");

		g11.addIEdge("N","I");
		g11.addIEdge("P","N");
		g11.addIEdge("Q","P");

		Graph before11 = new Graph();

		before11.addIEdge("?O","?C");
		before11.addIEdge("?R","?O");//if these 3 nodes are after the other nodes then there's no problem
		before11.addIEdge("?S","?R");

		before11.addIEdge("?A","?B");
		before11.addIEdge("?B","?C");
		before11.addIEdge("?C","?D");
		before11.addIEdge("?D","?E");
		before11.addIEdge("?E","?M");
		before11.addIEdge("F","?M");

		Rule rule11 = new Rule(before11, new Graph());

		System.out.println(
				test(rule11, g11, printAll, 1, number++));

		System.out.println("----------\nTEST 12: load the microwave graph: microwave.triple\n----------\n");		

		File f = new File("microwave.triple");
		TripleReader reader = new TripleReader();
		Graph microwave = reader.loadGraph(f);

		if(printAll)
			System.out.println(microwave);

		System.out.println("----------\nTEST 13: actorSendSignal rule\n----------\n");

		before = reader.loadGraph(new File("Rule files/actorSendSignal_Before.triple"));
		after = reader.loadGraph(new File("Rule files/actorSendSignal_After.triple"));

		rule = new Rule(before, after);

		if(printAll) {
			microwave.outputdot("original_microwave");
		}
		
		System.out.println(
				test(rule, microwave, printAll, 3, number++));
		
		PatternMatch oneMatch = 
			rule.findPatternMatch(microwave, new PatternMatch());

		rule.applyPatternMatch(oneMatch);
		
		rule.transform(microwave);
				
		if(printAll) {
			microwave.outputdot("microwave_after_send_signal");
		}

		System.out.println("----------\nTEST 14: simple multiple solutions test\n----------\n");		
		Graph input = new Graph();
		input.addIEdge("A", "B");
		input.addIEdge("B", "C");
		input.addIEdge("C", "G");
		input.addIEdge("C", "F");

		before = new Graph();
		before.addIEdge("A", "?D");
		before.addIEdge("?D", "C");
		before.addIEdge("C", "?E");

		Rule r = new Rule(before, new Graph());

		System.out.println(
				test(r, input, printAll, 2, number++));

		System.out.println("----------\nTEST 15: dealg and realg\n----------\n");		

		if(printAll) {
			System.out.println("alg:" + g11);
			Graph dealged = g11.dealg();
			System.out.println("dealg:"+dealged);
			Graph realged = dealged.realg();
			System.out.println("realg:"+realged);
		}

		System.out.println("----------\nTEST 15: elementary variable edge problem\n----------\n");		

		Graph t16 = new Graph();
		t16.addIEdge("B", "A");
		t16.addEdge("B", "A", "in");

		before = new Graph();
		before.addEdge("B", "A", "?c");

		after = new Graph();
		after.addEdge("B", "A", "?c");
		//after.addIEdge("B", "D");
		after.addEdge("B", "D", "?c");


		r = new Rule(before, after);

		System.out.println(test(r, t16, printAll, 2, number++));

		System.out.println("----------\nTEST 16: variable edges and nodes\n----------\n");

		before = new Graph();
		before.addEdge("?B", "A", "?z");

		Graph g16 = new Graph();
		g16.addEdge("E", "A", "in");
		g16.addIEdge("C", "A");

		after = new Graph();
		after.addEdge("D", "A", "?z");

		rule = new Rule(before, after);

		System.out.println(
				test(rule, g16, printAll, 2, number++));
		
		System.out.println("----------\nTEST 17: 10 possible pattern matches\n----------\n");
		
		Graph g17 = new Graph();
		g17.addIEdge("C", "A");
		g17.addIEdge("F", "A");
		g17.addIEdge("G", "A");
		g17.addIEdge("G", "B");
		g17.addIEdge("H", "B");
		g17.addIEdge("A", "E");
		g17.addIEdge("B", "E");
		
		before = new Graph();
		before.addIEdge("?D", "E");
		before.addIEdge("?J", "?D");
		before.addIEdge("?K", "?D");
		
		rule = new Rule(before, new Graph());
		
		System.out.println(
				test(rule, g17, printAll, 10, number++));
		
		System.out.println("----------\nTEST 18: Accept event with action rule\n----------\n");

		before = reader.loadGraph(
				new File("Rule files/acceptEventWithAction_Before.triple"));
		after = reader.loadGraph(
				new File("Rule files/acceptEventWithAction_After.triple"));

		rule = new Rule(before, after);
		
		System.out.println(
				test(rule, microwave, printAll, 1, number++));
		
		System.out.println("----------\nTEST 19: Accept event without action rule (this works for the wrong reasons)\n----------\n");
		
		before = reader.loadGraph(
				new File("Rule files/acceptEventNoAction_Before.triple"));
		after = reader.loadGraph(
				new File("Rule files/acceptEventNoAction_After.triple"));
		forbidden = reader.loadGraph(
				new File("Rule files/acceptEventNoAction_Forbidden.triple"));
		
		rule = new Rule(before, after, forbidden);
		
		System.out.println(
				test(rule, microwave, printAll, 0, number++));
	}


	private static String test(Rule rule, Graph graph, 
			Boolean printAll, Integer expectedFits, Integer testNum) {

		ArrayList<PatternMatch> matches = rule.findAllPatternMatches(graph);
		Integer number = matches.size();
		String str = "";
		if(printAll) {
			if(graph.size() < 100)
				str += graph + "\n" + rule + "\n";
			else
				str += rule + "\n";

			str += number + " fits\n";
		}
		int count = 0;
		if(printAll) {
			for(PatternMatch match : matches) {
				count++;
				str += "match " + count + ": " + match+"\n";
			}
		}
		
		if(!expectedFits.equals(number)) {
			str += "TEST FAILED: expected " + expectedFits
			+ " fits but found " + number + " fits"; 
		}
	/*	if(expectSome && number == 0) {
			str += "TEST FAILED in " + rule + "\n" + graph;
		}
		else if(!expectSome && number > 0) {
			str += "TEST FAILED 2 in " + rule;
		}*/

		if(rule.afterExists()
				&& !str.contains("TEST FAILED")
				&& printAll
				&& graph.size() < 100) {

			count = 0;
			Graph inputCopy;
			Rule ruleCopy;
			str += "\n";
			for(PatternMatch match : matches) {
				inputCopy = new Graph(graph);
				ruleCopy = new Rule(
						new Graph(rule.getBefore()), new Graph(rule.getAfter()));

				ruleCopy.getBefore().applyPatternMatch(match);
				ruleCopy.getAfter().applyPatternMatch(match);

				ruleCopy.transform(inputCopy);

				count++;
				str += "fit " + count + " is " + inputCopy;

				String name = "Test " + testNum + " fit " + count;
				name = name.replaceAll(" ", "_");
				inputCopy.outputdot(name);
				
				/*to check that nodes' internal representation
				  makes sense

				for(Edge e : inputCopy) {
					Node n = e.to;
					for(Edge e2 : n.getFrom())
						System.out.println("!"+n.getName() 
								+ " " + e2.label + " " + e2.toName);

					Node n2 = e.from;
					for(Edge e2 : n2.getTo())
						System.out.println("@"+e2.fromName + 
								" " + e2.label + " " + n2.getName());
				}*/
			}
		}	


		if(!str.contains("TEST FAILED"))
			str += "TEST PASSED\n";
		return str;
	}
}
