-- simple tokeniser for list-graph project parsers
-- Greg, May 2009
module Tokenizer where

tokenize :: String -> [String]
tokenize "" = []
tokenize string = 
    token:(tokenize rest)
        where (token,rest):others = lex string
