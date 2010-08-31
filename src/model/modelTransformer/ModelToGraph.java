package model.modelTransformer;

import java.net.URL;

import model.ListGraph;
import model.exceptions.ModelToGraphException;

/**
 * Transforms a model into a graph.
 * 
 * @author Kevin O'Shea
 */
public interface ModelToGraph {
	public ListGraph buildGraph(URL modelURL, URL instanceURL) throws ModelToGraphException;
}
