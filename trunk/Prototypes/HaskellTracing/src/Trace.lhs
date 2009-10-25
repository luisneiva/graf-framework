%% to get 2 modes happening, do 
%% M-x mmm-ify-by-class, and tell it literate-haskell-latex
\section{System Traces}
\label{trace}
The previous section developed code for representing, manipulating and
displaying list-graphs.  In this section, we build on this to enable
construction and display of sequences of list-graphs.  Since the
purpose of these sequences is to exhibit the behaviour of a modelled
system, we call them system traces.

Each system state transition is given as a pair of lists of edges.
The first list is the edges to add to the input graph, the second list
is the edges to delete.  

Of course, for each edge $(source, label, target)$ to be added or
deleted, we should also specify the position it should be added at or
deleted from.  It could be added anywhere in the list of $label$ edges
leaving $source$, and there may be several such edges that are
candidates for deletion.  For now, we add edges at the first position,
and remove the first matching edge.  This is sufficient for UML models
whose Properties are unordered, the default value.  To handle the full
generality of list-graph transitions, the module will need a minor
rewrite, adding positions to the edge specifications and using the
positional versions of the edge add and delete functions.  

In order to easily produce and display a sequence of graphs from a
sequence of updates, we have developed the fuction {\tt trace}.  It
takes a graph and a list of updates, where an update is a pair of
lists of edges, the adds and the deletes.  It writes the resulting
sequence of graphs as {\tt state0.dot} ... {\tt state$n$.dot}.

% Why oh why do you write them out here?
% separate production of the trace from committing it to disk!!
\begin{code} 
module Trace where

import Graph
import System
import List   -- for nub, which removes duplicates

type GUpdateList n l = [([GEdge n l],[GEdge n l])]

traceHelper :: (Show n, Show l, Eq n, Eq l) => 
    (GGraph n l, Int) -> GUpdateList n l -> IO (GGraph n l, Int)

traceHelper (graph, i) ((adds,deletes):restUpdates) = 
    do
        writeFile ("state" ++ show i ++ ".dot") (dotGraph graph)
        traceHelper (graph'', i+1) restUpdates
        where
           graph'  = (foldr (.) id (map addEdge adds)) graph
           graph'' = (foldr (.) id (map deleteEdge deletes)) graph'

traceHelper (graph, i) [] = 
    do
        writeFile ("state" ++ show i ++ ".dot") (dotGraph graph)
        return (graph, i+1)

trace :: (Show n, Show l, Eq n, Eq l) => GGraph n l -> GUpdateList n l -> IO ()
trace graph updates = 
    do 
        traceHelper (graph, 0) updates
        return ()

\end{code}

\subsection{Display}
We wish to display traces as sequences of graphs.  These graphs will
be quite large, for example, the graph for the initial system state
for the very simple example UML model has almost 200 edges.  We
implement a couple of techniques to help make visual sense of these
sequences of large graphs.  

First, to ensure that nodes and edges do not move around from one
state to the next, we create a union graph that has every node and
edge from the whole sequence.  Instead of a new graph for each state,
we recycle this graph, marking the currently existing edges.  

The second aid to sequence comprehension also employs this marking
idea.  Newly created nodes and edges are displayed in green, those
that are about to be deleted in red.  To enable this, we add a couple
of extra markings.

These existence status ``markings'' are implemented as a datatype {\tt
  Existence}.  ``Existential'' edges are a pair with an
existential-status and a familiar $(source, label, target)$ triple.

Existential status values correspond to display attributes for dot.
This correspondence is implemented as the function {\tt
  existentialAttribs}.  The pretty printing functions we saw in
Section \ref{graph}, for ``unmarked'' triples, are rewriten in a
mostly obvious way.  

One difference is that we explicitly mention the nodes, so we can give
them display attributes.  Since the nodes are not explicitly
represented, it takes a bit of work to extract them and determine
their existential status.  The function {\tt existentialNodes} gives
each node the ``greatest'' existential status of its (incoming and
outgoing) edges.

\begin{code}

data Existence =  Void | Destroying | Creating | Persisting 
    deriving (Show, Eq, Ord)

type ExistentialGEdge n l = (Existence, GEdge n l)

existentialAttribs :: Existence -> String
existentialAttribs status = 
    case status of
      Creating -> " color = green, fontcolor = green "
      Persisting -> ""
      Destroying -> " color = red, fontcolor = red "
      Void -> " style = invis "

dotExistentialEdge :: (Show n, Show l) => ExistentialGEdge n l -> String
dotExistentialEdge (status,(source, label, target)) =
    unwords [show source, "->", show target, 
            "[label =", show label, igrey, attribs, "]"] 
        where igrey = if show label `elem` ["i", "\"i\""] && status == Persisting
                      then ", color=grey" 
                      else ""
              attribs = existentialAttribs status

existentialNodes :: (Eq n, Eq l) => 
                    [ExistentialGEdge n l] -> [(Existence,n)]
existentialNodes eedges = 
    let source (e,(s,_,_)) = (e,s)
        target (e,(_,_,t)) = (e,t)
        allENodes = (map source eedges) ++ (map target eedges)
        nakedNodes = nub [n | (_,n) <- allENodes ]
        status node = foldr max Void [e | (e,n) <- allENodes, n == node ]
    in [(status n,n) | n <- nakedNodes]

dotExistentialNode :: Show n => (Existence, n) -> String
dotExistentialNode (status, node) = 
    unwords [show node, "[", existentialAttribs status, "]"]

dotExistentialEdgeList :: (Eq n, Eq l, Show n, Show l) => 
                          [ExistentialGEdge n l] -> String
dotExistentialEdgeList el = 
    unlines ("digraph anonymous {":(dotNodes ++ dotEdges)) ++ "}"
        where
          dotNodes = map dotExistentialNode (existentialNodes el)
          dotEdges = map dotExistentialEdge el
\end{code}

We now have the ability to display graphs represented as lists of
existentially attributed edges.  Next we provide the means to create a
trace in this format.

\begin{code}
etraceHelper :: (Show n, Show l, Eq n, Eq l) => [[ExistentialGEdge n l]] ->
                [ExistentialGEdge n l] -> GUpdateList n l -> [[ExistentialGEdge n l]]
etraceHelper trace state ((adds, deletes): updates) =
    etraceHelper (deleting:adding:state:trace) state' updates
    where
      adding   = paint Void Creating adds state
      added    = paint Creating Persisting adds adding
      deleting = paint Persisting Destroying deletes added
      state'   = paint Destroying Void deletes deleting

etraceHelper trace state [] = state:trace


paint :: (Show n, Show l, Eq n, Eq l) => 
    Existence -> Existence -> [GEdge n l] -> 
    [ExistentialGEdge n l] -> [ExistentialGEdge n l]

paint fromStatus toStatus (edge:edges) eedges = 
    if 
        any (==(fromStatus,edge)) eedges
    then  
        let (front, (_,e):back) = break (==(fromStatus,edge)) eedges
        in paint fromStatus toStatus edges (front ++ (toStatus,e):back)
    else 
        error (unwords ["paint:", show fromStatus, show edge, "does not exist"])


paint _ _ [] edges = edges


etrace :: (Show n, Show l, Eq n, Eq l) => 
                [GEdge n l] -> GUpdateList n l -> [[ExistentialGEdge n l]]

etrace state updates = 
    reverse (etraceHelper [] (vadded ++ pstate) updates)
    where 
      vadded = map (\edge -> (Void,edge)) adds
      adds = concat (map fst updates)
      pstate = map (\edge -> (Persisting,edge)) state 

\end{code}

Finally, a utility to produce a sequence of .dot and .svg files from
                                                 an existential trace.

\begin{code}

visualise :: (Eq n, Eq l, Show n, Show l) => 
             [[ExistentialGEdge n l]] -> IO ()
visualise trace = visualiseHelper 0 trace

visualiseHelper :: (Eq n, Eq l, Show n, Show l) => 
                   Int -> [[ExistentialGEdge n l]] -> IO ()

visualiseHelper i (state:states) =
    do
      writeFile ("state" ++ show i ++ ".dot") (dotExistentialEdgeList state)
      system ("dot -Tsvg -o state" ++ show i ++ ".svg state" ++ show i ++ ".dot")
      visualiseHelper (i+1) states

visualiseHelper _ [] = return ()

\end{code}

\subsection{Future Work}
Develop and demonstrate proper list-graph (not bag-graph) traces.

Separate production of trace from writing it to disk.

% ----- Configure Emacs -----
%
% Local Variables: ***
% mode: latex ***
% mmm-classes: literate-haskell-latex ***
% End: ***