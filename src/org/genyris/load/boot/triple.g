## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  About triples...And the global graph
#

df tripleq (s p o) 
   triple s p o

Thing
   defmethod .putprop(p o)
      *global-graph*
           .put this p o
      the o
   defmethod .addprop(p o)
      *global-graph*
           .add(triple this p o)
      the o
   defmethod .getprop(p)
      *global-graph*
          .get this p
   defmethod .getprop-list(p)
      *global-graph*
          .get-list this p
   defmethod .get-properties()
      (this(.asGraph))
         .union
            *global-graph*
                .select this nil nil
        
        