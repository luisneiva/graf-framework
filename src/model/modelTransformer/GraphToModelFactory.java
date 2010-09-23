package model.modelTransformer;

/** 
 * Factory class for creating GraphToModel instances
 * 
 * @author Kevin O'Shea
 */
public class GraphToModelFactory {
	
	/** Creates and returns a GraphToModel instance */
	public static GraphToModel createGraphToModel() {
		return new GraphToObjDiag();
	}
}
