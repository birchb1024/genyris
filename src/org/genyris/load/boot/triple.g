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
    while body
        var t (left body)
        *global-graph*
           .add (triple (car t) (car (cdr t)) (car (cdr (cdr t))))
        body = (cdr body)
        
        