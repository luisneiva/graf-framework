package model.modelTransformer;

import java.net.URL;

import model.Graph;
import model.exceptions.ModelToGraphException;

/**
 * Transforms a model into a graph.
 * 
 * @author Kevin O'Shea
 */
public interface ModelToGraph {
	public Graph buildGraph(URL modelURL, URL instanceURL) throws ModelToGraphException;
}
