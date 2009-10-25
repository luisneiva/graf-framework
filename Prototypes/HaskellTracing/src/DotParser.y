-- Happy grammar file for .dot graph syntax
-- see http://www.graphviz.org/doc/info/lang.html for original grammar
-- Greg O'Keefe, April 2009

{
module DotParser where
}

%name dotParser
%tokentype { String }
%error { parseError }

%token
      '{'   { "{" }
      '}'   { "}" }
      ';'   { ";" }
      '['   { "[" }
      ']'   { "]" }
      '='   { "=" }
      ','   { "," }
      ARROW { "->" }
      DIGRAPH { "digraph" }
      ID    { $$ }

%%

graph: DIGRAPH '{' stmt_list '}'        { $3 }
     | DIGRAPH ID '{' stmt_list '}'     { $4 }

stmt_list: edge_stmt                    { [$1] }
         | edge_stmt ';'                { [$1] }
         | stmt_list edge_stmt          { $2:$1 }
         | stmt_list ';' edge_stmt      { $3:$1 }

edge_stmt: ID ARROW ID                  { ($1,"",$3) }
         | ID ARROW ID attr_list        { ($1,$4,$3) } 

attr_list: '[' a_list ']'               { $2 }

a_list: ID                              { "" }
      | ID '=' ID                       { if $1=="label" then $3 else "" }
      | a_list ID                       { $1 }
      | a_list ',' ID                   { $1 }          
      | a_list ',' ID '=' ID            { if $3=="label" then $5 else $1 }

{
parseError :: [String] -> a
parseError _ = error "Parse error"

}
