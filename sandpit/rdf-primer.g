^contact:person rdf:about $who
        contact:fullName $name
        contact:mailbox $email
        contact:personalTitle  $title 

^contact:person rdf:about $who
        contact:fullName $name
        contact:mailbox $email
        contact:personalTitle  $title 
        
@prefix ex "http.//some/example/"
ex:Person
    rdf:type g:Class
    g:description "This class yada yada yada..."
    g:superclass ex:Item
    
@prefix ex "http://example.org/schemas/vehicles"
@prefix rdf "http://example.org/schemas/vehicles"
ex:MotorVehicle       rdf:type          rdfs:Class 
ex:PassengerVehicle   rdf:type          rdfs:Class 
ex:Van                rdf:type          rdfs:Class 
ex:Truck              rdf:type          rdfs:Class 
ex:MiniVan            rdf:type          rdfs:Class 

ex:PassengerVehicle   rdfs:subClassOf   ex:MotorVehicle 
ex:Van                rdfs:subClassOf   ex:MotorVehicle 
ex:Truck              rdfs:subClassOf   ex:MotorVehicle 

ex:MiniVan            rdfs:subClassOf   ex:Van
ex:MiniVan            rdfs:subClassOf   ex:PassengerVehicle

ex:registeredTo
    rdf:type rdfs:Property
    rdfs:domain ex:MotorVehicle
    rdfs:range ex:Person
    

query (?a rdf:type rdfs:Class)
    print ?a

ex:Book     rdf:type      rdfs:Class
ex:author   
    rdf:type      rdf:Property
    rdfs:domain   ex:Book

describe
    rdf:about |http://publisher/books#12312|
    ex:author "Fred Nurke"

tag Alist
    rdf:about = (intern "http://publisher/books#12312")
    ex:author = "Fred Nurke"
    ex:author = "Fred Nurke"
    