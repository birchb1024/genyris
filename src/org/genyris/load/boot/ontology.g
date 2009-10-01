## Copyright 2009 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  In-built classes and concepts
#
description
    StandardClass type StandardClass
    Property type StandardClass
    Thing type Class
    
    type type Property
    type domain Resource
    type range Class
    subClassOf type Property
    range type Property
    domain type Property
    comment type Property
    subPropertyOf type Property
    label type Property
    
    Container type Class
    Bag subClassOf Container
    Alt subClassOf Container
    Seq subClassOf Container
    member type Property
    member domain Resource
    member range Resource
    
    ## ContainerMembershipProperty type Class
    ## ContainerMembershipProperty
    
    List type Class
    first type Property
    first domain List
    first range Resource
    rest type Property
    rest domain List
    rest range List
    
    Statement type Class
    subject type Property
    subject domain Statement
    predicate type Property
    predicate domain Statement
    object type Property
    object domain Statement
    
    seeAlso type Property
    seeAlso domain Resource
    seeAlso range Resource
    
    isDefinedBy type Property
    isDefinedBy domain Resource
    isDefinedBy range Resource
    
    value type Property
    value domain Resource
    value range Resource
    
    
    