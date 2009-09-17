package model.modelTransformer;

/** 
 * Factory class for creating ModelToGraph instances
 * 
 * @author Kevin O'Shea
 */
public class ModelToGraphFactory {
	
	/** Creates and returns a ModelToGraph instance */
	public static ModelToGraph createModelToGraph() {
		return new XMIToGraph();
	}
}
