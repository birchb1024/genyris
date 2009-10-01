## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  Asserts
#
defmacro assert (expression)
  template
     cond
        (null? ,expression)
            raise (list "assert failed on expression: " ^,expression)

defmacro assertEqual (a b)
  template
     cond
        (equal? ,a ,b)
        else
          raise (list "assert failed on expression: " ^,a ^,b "values:" ,a ,b)

