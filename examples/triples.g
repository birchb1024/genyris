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
#TODO random order from asTriples gets flaky assert:
assertEqual
   statements(.asTriples)
   list
      ~ (triple twenty ^type ^|http://people.org/type#age|)
      ~ (triple twenty ^units Years)

    