@prefix ex "http://example.org/schemas/vehicles#"
@prefix rdf "http://rdf.org/stuff#"
@prefix s "http://example.org/students#"

df description (&rest args) args

description Person
  type Class
  type Dictionary

description
  rdf:about $Person
  rdf:type rdf:Class

description
  rdf:about Person
  rdf:type rdf:Class

description
  rdf:about Age
  rdf:type rdfs:property
  rdfs:range Bignum
  rdfs:domain Person

description Age
  type Property
  range Bignum
  domain Person

description |http://example.org/courses/6.001|
  s:students
     rdf:Bag
        s:Amy
        |http://example.org/students/Mohamed|
        |http://example.org/students/Johann|
        |http://example.org/students/Maria|
        |http://example.org/students/Phuong|

description
  rdf:about
  s:DistributionSite
    rdf:Alt
        |ftp://ftp1.example.org/|
        |ftp://ftp2.example.org/|
        |ftp://ftp3.example.org/|


@prefix p "http://mine.org/person#"

description
   p:Name
      type Property
      range String
   p:Age
      type Property
      range Bignum

class p:Person(Thing)
   .description
      # type Class # implicit
      # subclassOf Thing # implicit
      hasProperty p:Name
      hasProperty p:Age

description
   Month
      type Class
      range (1 = 12)
      range
         Alt
             January
             February
             ...


