%% to get 2 modes happening, do 
%% M-x mmm-ify-by-class, and tell it literate-haskell-latex

% todo, use unwords and unlines to tidy up pretty printers,
% remove messy extra spaces and newlines 
\section{Graph Representations, Manipulation and Output} 
\label{graph}
This section presents the module {\tt Graph}.  Three different graph
types are defined, each useful for some particular purpose.  Graph
``navigation'' is implemented, that is, evaluation of expressions of
the form $node.label$. Graph manipulation is provided in the form of
edge add and delete functions.  A pretty printing function is
provided, which outputs graphs in the dot format described in Section
\ref{dotFormat}.  This enables us to view graphs ``graphically'' by
running them through dot and invoking a viewing program.

The code below imports the required modules.  In Section
\ref{inputSection}, we described two Happy generated parsers and their
lexer.  These are included to enable graphs to be read from text
files.  Display of graphs is also provided by pretty-printing graphs
to dot's input language, and calling dot using the {\tt system}
function, provided by the {\tt System} module.  Module {\tt List}
provides the function {\tt partition}, which we use to extract
association list entries for a given key.

\begin{code} 
module Graph where

import Tokenizer
import DotParser
import TripleParser

import System  
import List 
\end{code}

We introduce a type for list-valued association lists and use it to
define general graphs, whose nodes and labels can be any type.
However, everything we do from here on uses graphs whose nodes and
labels are strings, so we reserve ``{\tt Graph}'' for this type.  When
reading and writing graphs, it is convenient to convert them to lists
of edges, where an edge is a triple (sourceNode, label, targetNode).
General and string-specific versions of this type are defined.

\begin{code}
type Lasl k d = [(k, [d])] 

type GGraph n l = [(n, Lasl l n)] 
type GEdge n l = (n,l,n)

type Graph = GGraph String String
type Edge = GEdge String String
\end{code}


\subsection{Navigation}
UML has an instruction-set, which it calls its ``Actions''.  Among the
actions are {\tt ReadLinkAction} \cite[\S11.3.33]{UML22super} and {\tt
  ReadStructuralFeatureAction} \cite[\S11.3.37]{UML22super}.  These
actions read values from an attribute or association end, and are
often refered to as ``navigation''.  Action languages sometimes
express this using a dot notation, as in {\tt self.subordinate} to
read the collection of objects related to the current one across an
association called ``subordinate''.

Defining graphs as nested association lists, as we have done, enables
a very simple implementation of navigation.  Navigation uses a version
of the Haskell prelude function {\tt lookup}, which we have written
specifically for list-valued association lists.  Rather than return
{\tt Nothing} when nothing is found, we return the empty list.

\begin{code}

llookup :: (Eq a) => a -> Lasl a b -> [b]
llookup _ [] = []
llookup key ((k,l):rest) 
    | k == key  = l
    | otherwise = llookup key rest

navigate :: (Eq n, Eq l) => (GGraph n l) -> n -> l -> [n]
navigate graph node label = llookup label (llookup node graph)
\end{code}


\subsection{Display}
The following functions allow the user to view graphs.  Evaluating
{\tt showGraph g} writes the graph {\tt g} in {\tt .dot} format, has
dot render it as an {\tt .svg} and calls {\tt eog} (Eye of Gnome) with
the result.  Of course, the graphics format and viewer can be changed
to suit the local installation.

The function {\tt edgeList} supports this by converting the graph into
a list of (sourceNode, label, targetNode) triples, while {\tt dotEdge}
converts such a triple into an edge statement for dot.  

Since the parsers return graphs in edge-list form, the function
{\tt showEdgeList} is also provided.  To make it easy to display graph
files in plain-triple format, the briefly named function {\tt self}
(show edge list file) is also provided.  We hope that this does not
get confused with the ``self'' term of the action languages, used to
refer to the object executing the behavior it occurs in.

UML system states in the form of these graphs contain a great number
of edges showing that one node (object) is an instance of another.
These edges are labelled ``i'' by convention in \cite{myThesis}, and
{\tt dotEdge} displays them in grey to make the resulting graph more
readable.

\begin{code}

edgeList :: GGraph n l -> [GEdge n l]
edgeList graph = [ (s,l,t) | (s, lts) <- graph, (l,ts) <- lts, t <- ts]

dotEdge :: (Show n, Show l) => GEdge n l -> String
dotEdge (source, label, target) =
    concat [show source, " -> ", show target, 
            " [label = ", show label, igrey, "] \n"] 
        where igrey = if show label `elem` ["i", "\"i\""]
                      then ", color=grey" 
                      else ""

dotEdgeList :: (Show n, Show l) => [GEdge n l] -> String
dotEdgeList el = 
    "digraph anonymous {\n" ++ foldr (++) "}\n" (map dotEdge el) 

dotGraph :: (Show n, Show l) => GGraph n l -> String
dotGraph graph = dotEdgeList (edgeList graph)


--  I would like to generalise these two, but don't know how

showEdgeList :: (Show n, Show l) => [GEdge n l] -> IO ExitCode
showEdgeList el = 
    do
      writeFile "tmp.dot" (dotEdgeList el)
      system("dot -Tsvg -o tmp.svg tmp.dot")
      system("eog tmp.svg&")

showGraph :: (Show n, Show l) => GGraph n l -> IO ExitCode
showGraph graph = 
    do
      writeFile "tmp.dot" (dotGraph graph)
      system("dot -Tsvg -o tmp.svg tmp.dot")
      system("eog tmp.svg&")

self path = 
    do 
      fileContents <- readFile path; 
      showEdgeList ((tripleParser . tokenize) fileContents)


\end{code}

\subsection{Add and Delete Edges}
All graph modifications are acheived by adding and deleting edges.
Nodes are effectively a byproduct of their edges, and there are no
explicit operations to add and delete them.  To add a node, add some
edge to or from it.  To delete a node, delete all edges to and from
it.  This removes the troublesome question of what to do with
``dangling'' edges, they simply cannot arise.  It also makes it
impossible to have an isolated node.  This could be considered a
deficiency, be we think not. We have defined list-graphs essentially
as navigation values.  Since no isolated edge can ever occur in the
value of a navigation, there is no reason for them to exist.

The following code is for adding edges.  First a couple of helper
functions.  The function {\tt insertAt} adds an item to a list at a
given position. The function {\tt llookupAndRest} looks up a key in a
list-valued association list, returning the result as the first
element of a pair.  The second element of the pair is the association
list elements for other keys.  This makes it easier to manipulate the
entry for a given key, then put the result back into the remainder of
the list.

The add edge function enables us to write a simple conversion from
edge-list form to graph form.  It is called {\tt graph}.

\begin{code}

insertAt :: Int -> a -> [a] -> [a]
insertAt pos item list = 
    front ++ item:back 
        where (front,back) = splitAt (pos - 1) list

llookupAndRest :: Eq a => a -> Lasl a b -> ([b], Lasl a b)
llookupAndRest key asl = (llookup key asl,rest)
    where (_, rest) = partition (\(k,_) -> k==key) asl

addEdgeAt :: (Eq n, Eq l) => Int -> GEdge n l ->  GGraph n l -> GGraph n l
addEdgeAt pos (source,label,target) graph = 
    let
	(outgoing, otherNodes) = llookupAndRest source graph
	(targets, otherLabels) = llookupAndRest label outgoing    
	labelLists = (label, insertAt pos target targets):otherLabels
    in  (source, labelLists):otherNodes

addEdge :: (Eq n, Eq l) => GEdge n l -> GGraph n l -> GGraph n l
addEdge edge graph = addEdgeAt 1 edge graph

graph :: (Eq n, Eq l) => [GEdge n l] -> GGraph n l
graph el = (foldr (.) id (map addEdge el)) []

\end{code}

Now we handle edge deletion in a similar manner.

\begin{code}

deleteAt :: Int -> [a] -> [a]
deleteAt pos list = 
    let (front, victim:back) = splitAt (pos - 1) list in 
    front ++ back

deleteEdgeFun :: (Eq n, Eq l) => 
		  ([n] -> [n]) -> n -> l -> GGraph n l -> GGraph n l
deleteEdgeFun delfun source label graph =
    let (labList, otherNodes)    = llookupAndRest source graph
	(targetList,otherLabels) = llookupAndRest label labList 
	labelLists = (label, delfun targetList):otherLabels
    in  (source, labelLists):otherNodes

deleteEdgeAt :: (Eq n, Eq l) => Int -> n -> l -> GGraph n l -> GGraph n l
deleteEdgeAt pos source label graph = 
    deleteEdgeFun (deleteAt pos) source label graph

deleteEdge :: (Eq n, Eq l) => GEdge n l -> GGraph n l -> GGraph n l
deleteEdge (source, label, target) graph =
    deleteEdgeFun (delete target) source label graph

\end{code}


\subsection{Ideas for Further Work}

The edge display function {\tt dotEdge} currently displays "i" edges
grey.  This hard-coded behavior could be generalised as follows. A
list of (predicate, formatter) pairs could be accepted, so that the
first predicate satisfied by a given edge gets the format associated
with that predicate.  This might be a good way to display similar
kinds of edges similarly.



% ----- Configure Emacs -----
%
% Local Variables: ***
% mode: latex ***
% mmm-classes: literate-haskell-latex ***
% End: ***