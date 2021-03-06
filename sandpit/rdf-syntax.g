#dict
#   |http://purl.org/dc/elements/1.1/#Title|  = "A Book About Something"
#   |http://purl.org/dc/elements/1.1/#Author| = "A.N. Author"

@prefix rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
@prefix dc "http://purl.org/dc/elements/1.1/"

rdf:description # a macro
   rdf:about    "http-//en.wikipedia.org/wiki#Tony-Benn" # gets interned
   dc:Title     "Tony Benn"
   dc:Publisher "Wikipedia"

@prefix w "http-//en.wikipedia.org/wiki#Tony-Benn"
^w:Tony-Benn
   .triples
       ^rdf:Type ^rdf:Property

^dc:Title .vars
^dc:Title .rdf:Type

def rdf:makeDocument(rdf:Title rdf:Publisher) 23

@prefix my "http://mystuff/"
class my:Person()
tag my:Person "Pat"

@prefix rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
@prefix dc "http://purl.org/dc/elements/1.1/"
@prefix ex "http://example.org/stuff/1.0/"

@prefix ex2 "http://www.w3.org/TR/rdf-syntax-grammar"
quote
 ex2:
  dc:title "RDF/XML Syntax Specification (Revised)"
  ex:editor
    ex:fullname "Dave Beckett"
    ex:homePage "http://purl.org/net/dajobe/"

    
    
