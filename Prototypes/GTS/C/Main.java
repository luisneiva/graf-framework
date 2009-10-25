import java.io.File;


/* 
 *   Main
 *   This class simply executes code from other classes.
 *   At the moment the contents of main could be changed to
 *   whatever is needed.
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
 *   Contents of Main
 *
 *     1. main()
 *     -description: this class currently does nothing in particular
 *                   it's just used to test the other classes
 *     -inputs: none at the moment, but it will eventually have to
 *              take input from a model to graph transformation
 *     -outputs: at the moment it just prints information
 */



public class Main {
	
	public static void main(String[] args) {

		Graph traceTest = new Graph();
		Node n1 = new Node("A");
		Node n2 = new Node("B");
		Node n3 = new Node("C");
		Node n4 = new Node("D");
		Edge e1 = new Edge();
		Edge e2 = new Edge();
		Edge e3 = new Edge();
		e1.connect(n1, n4);
		e2.connect(n3, n1);
		e3.connect(n2, n3);
		traceTest.add(n1);
		traceTest.add(n2);
		traceTest.add(n3);
		traceTest.add(n4);
		
		Graph traceBefore = new Graph();
		Node bn1 = new Node("A");
		Node bn2 = new Node("C");
		Node bn3 = new Node(null);
		Edge be1 = new Edge();
		Edge be2 = new Edge();
		Edge be3 = new Edge();
		be1.connect(bn2, bn1);
		be2.connect(bn3, bn2);
		be3.connect(bn1, bn3);
		traceBefore.add(bn1);
		traceBefore.add(bn2);
		traceBefore.add(bn3);
		Rule r = new Rule(traceBefore, new Graph());
		
		traceTest.describe();
		System.out.println("\n\n");
		traceBefore.describe();
		System.out.println("\n\n");
				
		System.out.println("IS IT A SUBGRAPH???? (should be no) " + r.trace(traceTest));
		
		
		System.out.println("-----------------------------\nTEST 2");
		
		Graph g2 = new Graph();
		Node i1 = new Node("A");
		Node i2 = new Node("B");
		Node i3 = new Node("C");
		Node i4 = new Node("D");
		Node i5 = new Node("E");
		Edge ie1 = new Edge();
		Edge ie2 = new Edge();
		Edge ie3 = new Edge();
		Edge ie4 = new Edge();
		ie1.connect(i2, i1);
		ie2.connect(i5, i2);
		ie3.connect(i4, i5);
		ie4.connect(i3, i2);
		g2.add(i1);
		g2.add(i2);
		g2.add(i3);
		g2.add(i4);
		g2.add(i5);
		
		Graph b2 = new Graph();
		Node ba = new Node("A");
		Node bb =  new Node("B");
		Node bu1 = new Node(null);
		Node bu2 = new Node(null);
		Node bd = new Node("D");
		Edge bl1 = new Edge();
		Edge bl2 = new Edge();
		Edge bl3 = new Edge();
		Edge bl4 = new Edge();
		bl1.connect(bb, ba);
		bl2.connect(bu1, bb);
		bl3.connect(bu2, bb);
		bl4.connect(bd, bu2);//bu1
		b2.add(ba);
		b2.add(bb);
		b2.add(bu1);
		b2.add(bu2);
		b2.add(bd);
		
		Rule r2 = new Rule(b2, new Graph());
		g2.describe();
		System.out.println("\n\n");
		b2.describe();
		System.out.println("\n\n");
		System.out.println("IS IT A SUBGRAPH???? (should be yes) " + r2.trace(g2));
		
		
		
		System.out.println("-----------------------------\nTEST 3: object diagram");
		
		TripleReader reader = new TripleReader();
		
		Graph microwave = reader.loadGraph(new File("microwave.triple"));
		//microwave.describe();
		long time = System.currentTimeMillis();
		System.out.println(microwave.toObjectDiagram());
		System.out.println(System.currentTimeMillis() - time);
		
		/*
		Graph graph = new Graph();		
				
		Node classNode = new Node("Class");
		Node objNode = new Node("Other");
		Edge objToClass = new Edge("unknown label");
		objToClass.connect(classNode, objNode);
		
		Node stateedge = new Node("State");
		Edge statetoclass = new Edge(EdgeType.i);
		statetoclass.connect(classNode, stateedge);
		
		Edge edge2 = new Edge(EdgeType.unknown);
		edge2.connect(stateedge, classNode);
		
		graph.add(classNode);
		graph.add(objNode);
		graph.add(stateedge);
		
		Rule rule = new Rule();
		
		graph.describe();
		
		Graph afterRule = rule.evaluate(graph);
		
		System.out.println("----after----\n");
		if(afterRule != null)
			afterRule.describe();
		*/
		/*
		 
		 afterRule.add(new Node("EXTRA"));
		
		Graph g2 = new Graph();
		Node classNode2 = new Node("Class");
		Node obNode2 = new Node("anything");
		Edge e2 = new Edge(EdgeType.unknown);
		e2.connect(classNode2, obNode2);
		
		g2.add(classNode2);
		g2.add(obNode2);
		
		g2.describe();
		
		Graph aftrule2 = rule.evaluate(g2);
		
		System.out.println("the below should not contain extra");
		aftrule2.describe();
		*/
		
		
	/*	Graph g2 = new Graph();
		 
		Node n1 = new Node("Class");
		Node n11 = new Node("State");
		Edge e1 = new Edge();
		e1.connect(n1, n11);
		
		Node n2 = new Node("Class");
		Node n21 = new Node("State");	
		Edge e2 = new Edge();
		e2.connect(n2, n21);
		Edge e21 = new Edge();
		e21.connect(n2, n2);
		
		graph.add(n1);
		graph.add(n11);
	
		g2.add(n2);
		g2.add(n21);
		
		System.out.println("graph 1 is:\n");
		graph.describe();

		System.out.println("\ngraph 2 is:\n");
		g2.describe();
		
		System.out.println("\nis graph 1 a subgraph of graph 2? "+g2.subgraphOf(graph));
		System.out.println("is graph 2 a subgraph of graph 1? "+graph.subgraphOf(g2));		
		*/
		/*Node classnode = new Node("Class");
		Node microwave = new Node("Microwave");
		Node state = new Node("State");
		Node mic2 = new Node("miMicrowave");
		
		Edge mictoclass = new Edge(EdgeType.i);
		mictoclass.connect(classnode, microwave);
		
		Edge statetoclass = new Edge(EdgeType.i);
		statetoclass.connect(classnode, state);
				
		Edge mic2tomic1 = new Edge(EdgeType.i);
		mic2tomic1.connect(microwave, mic2);
		
		graph.add(classnode);
		graph.add(microwave);
		graph.add(state);
		graph.add(mic2);
		
		//graph.describe();
		
		Graph graphtwo = new Graph();
		graphtwo.add(new Node("Class"));*/
		
		/*Edge e1 = new Edge();
		e1.connect(classnode, classnode);
		
		graphtwo.add(classnode);
		graphtwo.add(microwave);
		graphtwo.add(state);
		graphtwo.add(mic2);*/
		
		
		//System.out.println("g1 subgraph of g2 " + graphtwo.subgraphOf(graph));
		//System.out.println("g2 subgraph of g1 " + graph.subgraphOf(graphtwo));
	}

}
