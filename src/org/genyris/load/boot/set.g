## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  An set stored in a list
#
class SetList()

   def .equal?(A B)
      cond
         (equal? (length A) (length B))
            var result true
            while A
                 setq result
                    and
                       member? (car A) B
                       result
                 setq A (cdr A)
            result

   assert
      .equal? nil nil
   assert
      .equal? ^(1) ^(1)
   assert
      .equal? ^(1 2) ^(1 2)
   assert
      .equal? ^(2 1) ^(1 2)
   assert
      not (.equal? ^(2) ^(3))
   assert
      not (.equal? ^(2) ^(2 3))
