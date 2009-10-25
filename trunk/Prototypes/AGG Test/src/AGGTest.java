import java.util.ArrayList;

import agg.xt_basis.GraTraEventListener;
import agg.xt_basis.Node;

public class AGGTest {

	public static void main(String[] args) {

		ArrayList<AGGAssignment> params = new ArrayList<AGGAssignment>();
		//params.add(new AGGAssignment("selfRead","cBEB"));
		params.add(new AGGAssignment("inParam", "thefeature"));
	//	params.add(new AGGAssignment("inParam", "const"));
		
		String filename;
	//	filename = "testParameters.ggx";
	//	filename = "testAGGRules.ggx";
		filename = "testAGGRuleParam.ggx";
		
		AGGGraph gr;
		gr = new AGGGraph(filename, params);
				
		System.out.println(gr.toString());

		GraTraEventListener l = new MyGraTraEventListener();

	//	gr.transform(l); // works
	//	gr.myTransform(l); // works
		gr.apply(l); // works

		System.out.println("after transfr \n"+gr.toString());

		ArrayList<String> trace = new ArrayList<String>();
		trace.add("\"i\"");
		Node startNode = gr.getNodeByName("const2");
		ArrayList<Node> resu = gr.fromTrace(trace, startNode);
		//System.out.println(resu.size());
		for(Node node : resu)
			System.out.println(">" + AGGGraph.getGraphObjectName(node));
		
		//Node node = gr.getNodeByName("const");
		//System.out.println(node);
		
		System.out.println(gr.toTrace(trace, resu.get(0)).get(0));
	}

}
