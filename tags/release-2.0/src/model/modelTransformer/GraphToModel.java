package model.modelTransformer;

import model.Graph;
import model.modelTransformer.objectDisplay.ObjectDisplay;

/**
 * Transform a graph into a model structure, suitable for display.
 */
public interface GraphToModel {
	
	/** Reset data from previous animations */
	public void reset();
	
	public ObjectDisplay generateDisplayObjects(Graph graph);
}
