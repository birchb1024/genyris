## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  About triples...
#
df tripleq (s p o) (triple s p o)
var *global-graph* (graph)
df description(&rest body)
    for t in body
        *global-graph*
           .add (triple (nth 0 t) (nth 1 t) (nth 2 t))
        
        