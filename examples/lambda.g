## Copyright 2008, 2009 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
defvar ^last
  lambda (x)
    cond
      (eq? nil (cdr x))
         car x
      else
         last (cdr x)


assert 
    equal? 6 (last ^(1 2 3 4 5 6))
