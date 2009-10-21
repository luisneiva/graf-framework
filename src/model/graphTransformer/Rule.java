package model.graphTransformer;

/** 
 * Rule
 * A Rule is a before Graph and an after Graph.
 * If the before graph matches the input, by being a
 * subgraph of it, then it can be transformed 
 * into the after Graph. However, the output will 
 * be the after Graph PLUS the the content on the 
 * input, not on the before Graph.
 *
 * For example: if the input is N1 - N2 (the - is some irrelevant edge)
 * and the before graph is N1
 * and the after graph is N3
 * then the output will be N3 - N2
 *
 * There may also be nodes that must not be in the input.
 * There's more detail on the wiki page Graphs
 *
 * @author Oscar Wood 
 *     
 */



import java.util.ArrayList;
import java.util.Iterator;

import model.Edge;
import model.Graph;
import model.Node;


public class Rule {

	private Graph before;
	private Graph after;

	// forbidden contains nodes that must not exist in
	// the before graph.
	// If the input has an edge that is in forbidden
	// then it is not a subgraph.
	// Edges in forbidden will not all be connected to each other.
	private Graph forbidden;

	public Rule(Graph before, Graph after) {
		this(before, after, new Graph());
	}

	/** When a new node name needs to be generated that
	 * will be added in the after graph, this number
	 * will be appended to the name to make it unique.
	 */
	private static Integer idAppend = 0;

	public Rule(Graph before, Graph after, Graph forbidden) {
		this.before = before;
		this.after = after;
		this.forbidden = forbidden;

		for(Edge edge : after) {
			if(edge.getToName().charAt(0) == '*') {
				edge.giveToUniqueName(idAppend++);
			}
			if(edge.getFromName().charAt(0) == '*') {
				edge.giveFromUniqueName(idAppend++);
			}
		}
	}

	public String toString()
	{
		String str;
		str = "Before: \n" + before
		+ "\nAfter\n" + after;
		if(forbidden.size() > 0)
			str += "\nForbidden in before:\n" + forbidden;
		return str;
	}

	/**
	 * Tests whether a rule can be applied.
	 */
	private Boolean subgraph(Graph input, PatternMatch givenAssignments) {

		if(input.size() < before.size()) {
			//System.out.println("(The before graph is bigger than the input so it couldn't possibly be a subgraph)");
			return false;
		}
		
		Rule copy = new Rule(new Graph(before), 
				new Graph(), new Graph(forbidden));

		PatternMatch fit = copy.findPatternMatch(input, null, givenAssignments);
		Integer size = fit.size();

		Boolean check1 = size > 0;
		if(!check1) {
			return false;
		}

		Boolean check2 = copy.before.containsVariableNodes();
		if(check2) {
			return false;
		}

		Boolean check3 = copy.compareEdges(input);
		if(!check3) {
			return false;
		}

		Boolean check4 = copy.checkForbiddenEdges(input);
		if(!check4) {
			return false;
		}

		return true;
	}

	/**
	 *  Ensures the input doesn't contain any edges that are forbidden.
	 * @return False if forbidden edges prevent this being a pattern match.
	 */
	private Boolean checkForbiddenEdges(Graph input) {
		if(forbidden.size() > 0) {
			for(Edge edge : forbidden) {

				for(Edge inEdge : input) {
					if(inEdge.equals(edge)) {
						//	System.out.println("(forbidden edge " + edge + " was present)");
						return false;
					}
				}
			}
			return true;
		}
		else {
			return true;
		}
	}

	/**
	 * Applies the transformation to the input.
	 * This happens AFTER all variables have been solved. That is,
	 * after applyPatternMatch() is called.
	 * 
	 * @param input The graph to transform, which we assume has
	 * passed the subgraph test and has no variables.
	 */
	public void transform(Graph input) {
		Graph delete = new Graph();

		//List of groups of to, label and from as strings
		ArrayList<String[]> add = new ArrayList<String[]>();

		// Make a list of edges to add from edges
		// in after but not in before.
		for(Edge aftEdge : after) {
			Boolean inBefore = false;
			for(Edge befEdge : before) {
				if(befEdge.equals(aftEdge)) {
					inBefore = true;
					break;
				}					
			}
			if(!inBefore) {
				String to = aftEdge.getToName();
				String from = aftEdge.getFromName();
				String label = aftEdge.getLabel();

				String[] array = {from, label, to};
				add.add(array);

				//add.add(aftEdge);
			}
		}

		// Make a list of edges to delete from
		// edges in before but not in after.
		for(Edge befEgde : before) {
			Boolean inAfter = false;
			for(Edge aftEdge : after) {
				if(aftEdge.equals(befEgde)) {
					inAfter = true;
					break;
				}
			}
			if(!inAfter) {
				delete.add(befEgde);
				//System.out.println("REM " + befEgde);
			}
		}

		input.removeAll(delete);

		for(String[] array : add) {
			String from = array[0];
			String label = array[1];
			String to = array[2];
			input.addEdge(from, label, to);
			//System.out.println("ADD "+ from +" " + label+" " + to );
		}

		// Now fix up dangling nodes
		ArrayList<String> deleteNodes = new ArrayList<String>();
		ArrayList<String> befNodes = new ArrayList<String>();
		ArrayList<String> aftNodes = new ArrayList<String>();

		// List all nodes in after.
		for(Edge aftNode : after) {
			String to = aftNode.getToName();
			String from = aftNode.getFromName();
			if(!aftNodes.contains(to)) {
				aftNodes.add(to);
			}
			if(!aftNodes.contains(from)) {
				aftNodes.add(from);
			}
		}

		// List all nodes in before.
		for(Edge befNode : before) {
			String to = befNode.getToName();
			String from = befNode.getFromName();
			if(!befNodes.contains(to)) {
				befNodes.add(to);
			}
			if(!befNodes.contains(from)) {
				befNodes.add(from);
			}
		}

		// Find nodes in the before list, not in the after list.
		for(String befNode : befNodes) {
			Boolean present = false;
			for(String aftNode : aftNodes) {
				if(aftNode != null 
						&& befNode != null 
						&& aftNode.equals(befNode)) {
					present = true;
					break;
				}
			}
			if(!present) {
				deleteNodes.add(befNode);
			}	
		}

		for(Edge edge : input) {
			edge.getTo().deleteNodeReferences(deleteNodes);
			edge.getFrom().deleteNodeReferences(deleteNodes);
		}

		// Now delete from the transformed graph the nodes
		// in the before but not in the after, along with 
		// their edges.
		Iterator<Edge> dangleIterator = input.iterator();
		while (dangleIterator.hasNext()) {
			Edge edge = dangleIterator.next();
			Boolean present = false;

			for(String node : deleteNodes) {
				if(edge.getToName().equals(node)
						|| edge.getFromName().equals(node)) {
					present = true;
					break;
				}
			}
			if(present) {
				dangleIterator.remove();
			}
		} 

		//input.refresh(); not needed
	}

	/**
	 * If a rule has any variable nodes, then this must be 
	 * called before transform().It changes the variable edges 
	 * in the rule into non-variable edges that match edges in
	 * the input. THIS PERMANENTLY CHANGES THE RULE TO WORK ON 
	 * ONLY ONE INPUT. In the case where multiple pattern matches
	 * are possible this will find just any one of them.
	 * 
	 * @param input A graph with no variable nodes.
	 * 
	 * @param ignoreMatch another pattern match such that this
	 * method will return a pattern match that contains
	 * nothing in the input
	 * 
	 * @param forceMatch The variable assignments in this will immediately be filled in.
	 * 
	 * @return A list of VariableAssignment representing the
	 * changes made to the graph. If the size of this list is 
	 * zero then the rule could not be applied.
	 */
	private PatternMatch findPatternMatch(Graph input,
			PatternMatch ignoreMatch,
			PatternMatch forceMatch) {

		PatternMatch result = new PatternMatch();

		ArrayList<String> likelyNodes = new ArrayList<String>();
		if(forceMatch != null) {
			for(VariableAssignment va : forceMatch) {
				NodeVariableAssignment nva = (NodeVariableAssignment) va;
				likelyNodes.add(nva.inputNode.getName());
			}
		}

		// Do not consider any variable assignments
		// that appear in this list. Instantly toss them out.
		PatternMatch ignore = new PatternMatch();
		if(ignoreMatch != null) {
			for(VariableAssignment va : ignoreMatch) {
				ignore.add(va);
			}
		}

		// The list constants is a list of all node names
		// in before and after, that are constant.
		// When a variable node is turned into a constant,
		// that node is also added to this list.
		// (We don't want a variable in before to be changed into
		// a constant node that ALREADY exists in before)
		ArrayList<String> constants = new ArrayList<String>();

		for(Edge e : before) {
			if(!e.toIsVariable
					&& !constants.contains(e.getToName())) {
				constants.add(e.getToName());
			}
			if(!e.fromIsVariable
					&& !constants.contains(e.getFromName())) {
				constants.add(e.getFromName());
			}
		}
		for(Edge e : after) {
			if(!e.toIsVariable
					&& !constants.contains(e.getToName())) {
				constants.add(e.getToName());
			}
			if(!e.fromIsVariable
					&& !constants.contains(e.getFromName())) {
				constants.add(e.getFromName());
			}		
		}

		// A list of tuples of variable nodes and a possible
		// constant node that it fits in one or more
		// rule applications.
		PatternMatch possibilities = new PatternMatch();

		// Check if an edge with 2 variables requires us to go again.
		Boolean loopAgain = false;

		// Make sure we're not stuck in a loop if we loop again.
		Boolean progressMade = false;

		// Only look at pairs as easy as the first pair in the
		// list (which is sorted by difficulty). Leave other
		// pairs for further iterations of the do...while
		// loop (by then they'll be less difficult).
		Integer maximumDifficulty;

		// We don't want to assign different variables to a single
		// constant. This keeps track of variables assigned
		// to constants.
		ArrayList<String> takenConstants = new ArrayList<String>();

		// Take care of the assignments given.
		if(forceMatch != null) {
			for(VariableAssignment va : forceMatch) {
				NodeVariableAssignment nva = (NodeVariableAssignment) va;

				result.add(va);

				Node var = nva.ruleNode;
				Node possib = nva.inputNode;

				//String target = nva.ruleNode.getName();
				//String rep = nva.inputNode.getName();

				//	System.out.println("(*FORCE* REPLACE " + nva.ruleNode
				//				+ " with " + nva.inputNode+")");


				//	for(Edge e : before)
				//		System.out.println("]]]"+e);

				String target = var.getName();
				String rep = possib.getName();

				before.replaceNode(target, rep);
				after.replaceNode(target, rep);


				if(forbidden.size() > 0) {
					forbidden.replaceNode(target, rep);
				}

				constants.add(nva.inputNode.getName());

				if(takenConstants.contains(nva.inputNode.getName())) {
					System.out.println("NEW error: " + 
							nva.ruleNode.getName() + " and another var node "
							+ "both assigned to " + nva.inputNode.getName() + " in rule.java");
				}

				takenConstants.add(nva.inputNode.getName());

			}
		}

		possibilities.addAll(findSuspiciousPairs(input, likelyNodes));

		do {

			progressMade = false;
			maximumDifficulty = null;

			Iterator<VariableAssignment> susPairIt = possibilities.iterator();
			mainloop : while (susPairIt.hasNext())
			{
				NodeVariableAssignment pair 
				= (NodeVariableAssignment) susPairIt.next();

				Node var = pair.ruleNode;
				Node possib = pair.inputNode;

				if(maximumDifficulty == null) {
					maximumDifficulty = var.getCertainty();
				}

				// Check if we are supposed to ignore
				// this assignment.
				for(VariableAssignment varAssign : ignore) {
					NodeVariableAssignment va = (NodeVariableAssignment) varAssign;
					if(pair.equals(va)) {
						susPairIt.remove();
						continue mainloop;
					}
				}

				// Check if the edge has already been solved
				// in another suspicious pair.
				if(!var.variable()) {
					susPairIt.remove();
					continue;
				}

				// Check if we assigned the constant node
				// to some other variable.
				if(takenConstants.contains(possib.getName())) {
					susPairIt.remove();
					continue;
				}

				if(!maximumDifficulty.equals(var.getCertainty())) {
					//System.out.println("skip " + pair + " " + var.getCertainty());
					continue;
				}


				// Check we're not trying to assign to
				// a constant already taken.
				if(constants.contains(possib.getName())) {
					susPairIt.remove();
					continue;
				}

				// Check the edges to each of the nodes match.
				if(!pair.verifyEdges(input,before)) {					
					susPairIt.remove();
					continue;
				}

				// The pair is not impossible.
				// Assign the variable node to the constant.
				result.add(pair);

				progressMade = true;

				//	System.out.println("(REPLACE " + var.getName()
				//			+ " with " + possib.getName()+")");

				String target = var.getName();
				String rep = possib.getName();

				before.replaceNode(target, rep);
				after.replaceNode(target, rep);

				if(forbidden.size() > 0) {
					forbidden.replaceNode(target, rep);
				}

				likelyNodes.add(pair.inputNode.getName());

				constants.add(pair.inputNode.getName());

				if(takenConstants.contains(pair.inputNode.getName())) {
					System.out.println("error: " + 
							pair.ruleNode.getName() + " and another var node "
							+ "both assigned to " + pair.inputNode.getName() + " in rule.java");
				}

				takenConstants.add(pair.inputNode.getName());

				susPairIt.remove();

			}

			loopAgain = possibilities.size() > 0;

			if(loopAgain && progressMade) {
				sortPairs(possibilities, likelyNodes);
				//		System.out.println("LOOP ");
			}

		} while (loopAgain && progressMade);

		return result;
	}

	/**
	 * Internal calculation to identify what the variable fits 
	 * in the input.
	 * 
	 * @param input Either before or after.
	 * @return A list of variable assignments that may or may not be possible.
	 */
	private PatternMatch findSuspiciousPairs(Graph input, ArrayList<String> likelyNodes) {
		PatternMatch result = new PatternMatch();

		for(Edge e : before) {

			Node to = e.getTo();
			if(to.variable()) {
				for(Edge inEdge : input) {

					if(inEdge.getLabel().equals(e.getLabel())) {
						NodeVariableAssignment varAssign = new NodeVariableAssignment(to, inEdge.getTo());
						if(varAssign.verifyEdges(input,before)) {

							if(!result.contains(varAssign)) {
								result.add(varAssign);
							}
						}
					}

				}
			}

			Node from = e.getFrom();
			if(from.variable()) {
				for(Edge inEdge : input) {

					if(inEdge.getLabel().equals(e.getLabel())) {
						NodeVariableAssignment varAssign = new NodeVariableAssignment(from, inEdge.getFrom());
						if(varAssign.verifyEdges(input,before)) {

							if(!result.contains(varAssign)) {
								result.add(varAssign);	
							}
						}
					}
				}		
			}
		}

		sortPairs(result, likelyNodes);

		//	for(VariableAssignment va : result)
		//		System.out.println("VA " + va + " " 
		//				+ ((NodeVariableAssignment) va).ruleNode.getCertainty());
		//	System.out.println("\n");

		return result;
	}

	/**
	 * Sorts a list of nodes according to the 
	 * attribute "certainty".
	 * 
	 * If anyone knows a better sorting algorithm, tell me.
	 */
	private static void sortPairs(PatternMatch list, ArrayList<String> likelyNodes) {

		for(VariableAssignment varAssign : list) {
			NodeVariableAssignment va = (NodeVariableAssignment) varAssign;
			Node variable = va.ruleNode;
			variable.resetCertainty();
		}

		for(VariableAssignment varAssign : list) {
			NodeVariableAssignment va = (NodeVariableAssignment) varAssign;
			Node variable = va.ruleNode;
			variable.calculateCertainty(likelyNodes);
		}

		//System.out.println("beginning to sort...");


		Integer value = null;
		VariableAssignment current;
		Boolean startAgain = false;
		Integer switchThis = null;
		do
		{
			if(switchThis != null)
			{
				VariableAssignment temp = list.get(switchThis);
				VariableAssignment temp2 = list.get(switchThis-1);
				list.set(switchThis, temp2);
				list.set(switchThis-1, temp);

				switchThis = null;
				startAgain = false;
			}
			for(int n = 0; n < list.size(); n++)
			{	
				current = list.get(n);
				NodeVariableAssignment nva = (NodeVariableAssignment) current;
				if(n != 0)
				{
					if(nva.ruleNode.getCertainty() > value)
					{
						switchThis = n;
						startAgain = true;
						break;
					}
				}
				value = nva.ruleNode.getCertainty();
			}
			value = null;
		}
		while(startAgain);

		/*String lastString = null;
		for(VariableAssignment va : list) {
			Node variable = va.ruleNode;
			if(lastString == null)
				lastString = variable.getName();
			if(!variable.getName().equals(lastString))
			{
				System.out.println("VVVVVV " + variable + " " + variable.getCertainty());
				lastString = variable.getName();
			}
		}*/
		//System.out.println("..ending sort");
	}

	/*public Boolean containsVariableNodes() {
		return before.containsVariableNodes()
		|| after.containsVariableNodes();
	}*/

	/**
	 * This assumes there are no variable edges.
	 * It simply checks that an edge in before is in the input.
	 */
	private Boolean compareEdges(Graph input) {
		for(Edge beforeEdge : before) {
			Boolean present = false;
			for(Edge inputEdge : input) {
				if(beforeEdge.equals(inputEdge)
						&& !inputEdge.checked) {
					inputEdge.checked = true;
					present = true;
					break;
				}
			}
			if(!present) {
				input.resetCheck();
				return false;
			}
		}
		input.resetCheck();
		return true;
	}

	/**
	 * Finds all possible pattern matches given the before 
	 * and the input.
	 * 
	 * Since the animator will be given parameters to narrow
	 * the search, we should only ever need one pattern match.
	 * I think therefore that this will be unused and the 
	 * generatePatternMacth() method will be used instead.
	 * 
	 * @param input The graph to find the pattern matches for.
	 * 
	 * @param givenAssignments Any variable assignments we know 
	 * will be in the solution, to limit the search. Ideally, if
	 * this isn't null, only a signle pattern match will be
	 * found.
	 * 
	 * @return A list of all possible ways the graph fits the
	 * rule. This will be an empty list if there're none.
	 */
	@Deprecated
	public ArrayList<PatternMatch> findAllPatternMatches(Graph input, 
			PatternMatch givenAssignments) {

		Boolean useDealg = before.containsVariableEdges();

		if(useDealg) {
			dealg();
			input = input.dealg();
		}

		ArrayList<PatternMatch> result = new ArrayList<PatternMatch>();

		if(!subgraph(input, givenAssignments)) {
			if(useDealg) {
				realg();
				input = input.realg();
			}
			return result;
		}

		Rule copy = new Rule(new Graph(before), 
				new Graph(after), new Graph(forbidden));

		PatternMatch firstMatch = copy.findPatternMatch(input, null, givenAssignments);

		result.add(firstMatch);

		for(VariableAssignment va : firstMatch) {
			PatternMatch toIgnore = new PatternMatch();
			toIgnore.add(va);
			copy = new Rule(new Graph(before), 
					new Graph(after),
					new Graph(forbidden));

			PatternMatch next = copy.findPatternMatch(input, toIgnore, givenAssignments);

			/*if(next.size() == 0)
				System.out.println(va + " didn't work");
			else
				System.out.println(va + " got us " + next);*/

			if(next.size() > 0 && !result.contains(next)) {

				Integer numVar = before.numberOfVariables();
				if(numVar.equals(next.size())) {
					result.add(next);
				}
			}
		}

		if(useDealg) {

			for(PatternMatch pm : result) {

				PatternMatch newAssignments = new PatternMatch();

				Iterator<VariableAssignment> varAssignIt = pm.iterator();
				while(varAssignIt.hasNext()) {
					VariableAssignment va = varAssignIt.next();
					NodeVariableAssignment nva = (NodeVariableAssignment) va;

					Boolean varIsEdge = nva.ruleNode.getName().contains("#");
					Boolean constIsEdge = nva.inputNode.getName().contains("#");

					if(varIsEdge && constIsEdge) {
						String constant = nva.inputNode.getName();
						String var = nva.originalVariable;
						newAssignments.add(
								new EdgeVariableAssignment(var, constant));
						//System.out.println("do something about " + nva);
						varAssignIt.remove();
					}
					else if(varIsEdge || constIsEdge) {
						System.out.println("huge error: variable assigned to node (findAllPatternMatches)");
					}
				}

				pm.addAll(newAssignments);
			}

			realg();
			input = input.realg();

			if(useDealg) {
				for(PatternMatch pm : result) {
					for(VariableAssignment va : pm) {
						if(va instanceof EdgeVariableAssignment) {
							EdgeVariableAssignment eva = (EdgeVariableAssignment) va;
							if(eva.getNeedsUpdate()) {
								eva.update(before, input);
							}
							else
								System.out.println("error in findAllPatternMatches");
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Finds the first possible complete list of assignments for the input,
       * given some of the assignments.
	 *
	 */
	public PatternMatch generatePatternMacth(Graph input, PatternMatch givenAssignments) {

		Integer numVars = before.numberOfVariables();
		
		Boolean useDealg = before.containsVariableEdges();

		if(useDealg) {
			dealg();
			input = input.dealg();
		}
		
		if(!subgraph(input, givenAssignments)) {
			if(useDealg) {
				realg();
				input = input.realg();
			}
			return null;
		}

		Rule copy = new Rule(new Graph(before), 
				new Graph(after), new Graph(forbidden));

		PatternMatch match = copy.findPatternMatch(input, null, givenAssignments);

		if(useDealg) {

			PatternMatch newAssignments = new PatternMatch();

			Iterator<VariableAssignment> varAssignIt = match.iterator();
			while(varAssignIt.hasNext()) {
				VariableAssignment va = varAssignIt.next();
				NodeVariableAssignment nva = (NodeVariableAssignment) va;

				Boolean varIsEdge = nva.ruleNode.getName().contains("#");
				Boolean constIsEdge = nva.inputNode.getName().contains("#");

				if(varIsEdge && constIsEdge) {
					String constant = nva.inputNode.getName();
					String var = nva.originalVariable;
					newAssignments.add(
							new EdgeVariableAssignment(var, constant));
					//System.out.println("do something about " + nva);
					varAssignIt.remove();
				}
				else if(varIsEdge || constIsEdge) {
					System.out.println("huge error: variable assigned to node (findAllPatternMatches)");
				}
			}

			match.addAll(newAssignments);

			realg();
			input = input.realg();
		}

		if(useDealg) {
			for(VariableAssignment va : match) {
				if(va instanceof EdgeVariableAssignment) {
					EdgeVariableAssignment eva = (EdgeVariableAssignment) va;
					if(eva.getNeedsUpdate()) {
						eva.update(before, input);
					}
					else {
						System.out.println("error in findAllPatternMatches");
					}
				}
			}
		}

		if(match.size() != numVars) {
			System.out.println("error in generatePatternMatch(): supposed to find " 
					+ numVars + " assignments, but found " + match.size());
		}

		return match;
	}

	/**
	 * Dealgrebrizes before, after and forbidden (see wiki page graphs).
	 */
	private void dealg() {
		before = before.dealg();
		after = after.dealg();
		forbidden = forbidden.dealg();
	}

	/**
	 * Undoes the effect of dealg().
	 */
	private void realg() {
		before = before.realg();
		after = after.realg();
		forbidden = forbidden.realg();
	}

	/**
	 * Once you've used generatePatternMatch() or
	 * findAllPatternMatches(), use the method to to actually
	 * change the graph according to the pattern match found.
	 * 
	 * @param pattern Created by generatePatternMatch() or findAllPatternMatches()
	 */
	public void applyPatternMatch(PatternMatch pattern) {
		before.applyPatternMatch(pattern);
		after.applyPatternMatch(pattern);
		forbidden.applyPatternMatch(pattern);		
	}

}
