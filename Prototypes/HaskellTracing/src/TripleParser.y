-- Grammar file for reading graphs from lists of 
--    sourceNode label targetNode
-- triples.  No brackets, no commas.  
-- This format is for ease of data entry.
-- Greg O'Keefe, May 2009

{
module TripleParser where
}

%name tripleParser graph
%name updateParser updates
%tokentype { String }
%error { parseError }

%token
      '{'    { "{" }
      '}'    { "}" }
      '*'    { "*" }
      WHITE  { "" }
      ADD    { "add" }
      DELETE { "delete" }
      ID     { $$ }

%%

updates: updates update   { $2:$1 }
       | updates WHITE    { $1 }
       | updates comments { $1 }
       | update           { [$1] }

update: comments ADD '{' maybeGraph '}' comments DELETE '{' maybeGraph '}' { ($4,$9) } 

maybeGraph: {- nothing -} { [] }
          | graph         { $1 }

graph: graph WHITE      { $1 }
     | graph comment    { $1 }
     | graph edge       { $2:$1 }
     | edge             { [$1] } 

edge: comments ID ID ID { ($2,$3,$4) }

comments: {- nothing -} { }
     | comments comment { }

comment: '{' '*' text '*' '}' { }

text: text ID       { }
    | ID            { }

{
parseError :: [String] -> a
parseError _ = error "Parse error"

}
