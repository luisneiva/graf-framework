import agg.xt_basis.GraTra;
import agg.xt_basis.GraTraEvent;
import agg.xt_basis.GraTraEventListener;


public class MyGraTraEventListener implements GraTraEventListener {
	
	
	public MyGraTraEventListener() {
	}
	
	public void graTraEventOccurred(GraTraEvent event) {
		
		
		if(event.getMessage() == GraTraEvent.ATOMIC_GC_FAILED) {
			System.out.println("ATOMIC_GC_FAILED");
		}
		if(event.getMessage() == GraTraEvent.ATTR_TYPE_FAILED) {
			System.out.println("ATTR_TYPE_FAILED");
		}
		if(event.getMessage() == GraTraEvent.CANNOT_TRANSFORM) {
			System.out.println("CANNOT_TRANSFORM");
		}
		if(event.getMessage() == GraTraEvent.DESTROY_MATCH) {
			System.out.println("DESTROY_MATCH");
		}
		if(event.getMessage() == GraTraEvent.GRAPH_FAILED) {
			System.out.println("GRAPH_FAILED");
		}
		if(event.getMessage() == GraTraEvent.GRAPH_INCOMPLETE) {
			System.out.println("GRAPH_INCOMPLETE");
		}
		if(event.getMessage() == GraTraEvent.INCONSISTENT) {
			System.out.println("INCONSISTENT");
		}
		if(event.getMessage() == GraTraEvent.LAYER_FINISHED) {
			System.out.println("LAYER_FINISHED");
		}
		if(event.getMessage() == GraTraEvent.MATCH_VALID) {
			System.out.println("MATCH_VALID");
		}
		if(event.getMessage() == GraTraEvent.NEW_MATCH) {
			System.out.println("NEW_MATCH");
		}
		if(event.getMessage() == GraTraEvent.NO_COMPLETION) {
			System.out.println("NO_COMPLETION");
		}
		if(event.getMessage() == GraTraEvent.PARAMETER_NOT_SET) {
			System.out.println("PARAMETER_NOT_SET");
		}
		if(event.getMessage() == GraTraEvent.TRANSFORM_STOP) {
			System.out.println("TRANSFORM_STOP");
		}
		if(event.getMessage() == GraTraEvent.TRANSFORM_START) {
			System.out.println("TRANSFORM_START");
		}
		if(event.getMessage() == GraTraEvent.TRANSFORM_FINISHED) {
			
			System.out.println("TRANSFORM_FINISHED");
			
			//System.out.println(">> " +agg.toString() + " <<" + event.getSource());
			
		//	GraTra gt = (GraTra) event.getSource();
		//	gt.stopRule();
		//	gt.stop();


		}
		if(event.getMessage() == GraTraEvent.TRANSFORM_FAILED) {
			System.out.println("TRANSFORM_FAILED");
		}
		if(event.getMessage() == GraTraEvent.STEP_COMPLETED) {
			System.out.println("STEP_COMPLETED");
			GraTra gt = (GraTra) event.getSource();
		//	gt.stopRule();
			gt.stop();
		}
		if(event.getMessage() == GraTraEvent.RULE_FAILED) {
			System.out.println("RULE_FAILED");
		}
		if(event.getMessage() == GraTraEvent.RESET_GRAPH) {
			System.out.println("RESET_GRAPH");
		}

	}
}
