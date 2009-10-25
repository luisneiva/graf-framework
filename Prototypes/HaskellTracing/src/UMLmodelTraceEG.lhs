%% to get 2 modes happening, do 
%% M-x mmm-ify-by-class, and tell it literate-haskell-latex

% todo: make this flow as just an example, once the trace code has been relocated

\section{Examples} 
This section presents applications of the graph tools developed in
Section \ref{graph} and the trace tools developed in Section
\ref{trace}.

The second subsection develops a trace of the UML model, whose initial
state graph has almost 200 edges.  Before diving into this level of
complexity, we present a much simpler example in the first subsection.


\subsection{Very Simple Example}
This small example models a very optimistic story of the authors
aquisition of European languages. First, we define an initial state,
then two updates.  These are used to construct a trace.

\begin{code}
module UMLmodelTraceEG where

import TripleParser
import Tokenizer
import Graph
import Trace

initialLang :: Graph
initialLang = [("Greg",[("learning",["French"])])]

edgesToAdd1, edgesToDelete1, edgesToAdd2, edgesToDelete2 :: [Edge]
edgesToAdd1 = [("Greg","learning","Italian"),("Greg", "speaks", "French")]
edgesToDelete1 = [("Greg","learning","French")]
edgesToAdd2 = [("Greg","speaks", "Italian"), ("Greg","learning","Spanish")]
edgesToDelete2 = [("Greg", "learning","Italian")]
langUpdates = [(edgesToAdd1, edgesToDelete1), (edgesToAdd2, edgesToDelete2)]

langTrace = trace initialLang langUpdates

\end{code}

The three states of this trace can be seen side by side in Figure \ref{dinky}.

\begin{figure}
\begin{center}
\includegraphics[height=30mm]{langState0.png}
\includegraphics[height=30mm]{langState1.png}
\includegraphics[height=30mm]{langState2.png}
\end{center}
\caption{Three successive graph-states}
\label{dinky}
\end{figure}

Now, we create a coloured, positionally stable visual animation of
this trace.
\begin{code}
egEtrace = etrace (edgeList initialLang) langUpdates 
\end{code}
To create a visualisation of this trace, load {\tt
  UMLmodelTraceEG.lhs} in {\tt ghci}, and evaluate {\tt visualise
  egEtrace}.  This will create files {\tt state0.svg} \ldots {\tt
  state6.svg}.  Eye of Gnome can be invoked to view all these files
with the command {\tt eog state*.svg}, and then the ``next'' button
can be used to step through them.


\subsection{Simple UML Example}
The motivation for list-graphs is formal semantics for
UML.  This section presents an execution trace for the example model
shown in Figure \ref{model}.  The trace is intended to satisfy
sequence diagram shown there.


\begin{figure}
\begin{center}
\includegraphics[width=\textwidth]{MicrowaveUML.pdf}
\end{center}
\caption{Example UML model}
\label{model}
\end{figure}


\begin{code}
vt triplesFile updatesFile =
  do
    tfileContents <- readFile triplesFile
    ufileContents <- readFile updatesFile
    let 
      iStateTriples = (tripleParser . tokenize) tfileContents 
      updates = reverse ((updateParser . tokenize) ufileContents)
      in
      visualise (etrace iStateTriples updates)
\end{code}

The initial state of our example is given an a plain-triple format
file, {\tt microwave.triple}, and the updates in {\tt microwave.updates}.

Now the trace visualised by evaluating {\tt vt "microwave.triple"
  "microwave.updates"} in ghci, then viewing the results with {\tt eog
  state*.svg} in the shell.

% ----- Configure Emacs -----
%
% Local Variables: ***
% mode: latex ***
% mmm-classes: literate-haskell-latex ***
% End: ***