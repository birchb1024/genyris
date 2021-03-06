@prefix rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
@prefix rdfs "http://www.w3.org/2000/01/rdf-schema#"
^rdf:Resource
    triples
      ^rdfs:isDefinedBy = "http://www.w3.org/2000/01/rdf-schema#"
      ^rdfs:label = "Resource"
      ^rdfs:comment = "The class resource, everything."

^rdf:class
   ^rdfs:comment = "The class of classes."
   ^rdfs:subClassof = ^rdfs:Resource
   
^rdf:class ^rdfs:comment "The class of classes."
   