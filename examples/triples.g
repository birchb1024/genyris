## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
class Years

## make statements about this object:
var twenty 20
var statements
   graph 
      `($twenty units $Years)
      `($twenty type |http://people.org/type#age|)
assertEqual
   statements(.asTriples)
   list (triple twenty ^units Years) (triple twenty ^type ^|http://people.org/type#age|)

    