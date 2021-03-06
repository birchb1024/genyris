## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
#
# The TAK benchmark
#

def tak (x y z)
        # print (list x y z) "\n"
        cond
           (< y x)
              tak
                  tak (- x 1) y z
                  tak (- y 1) z x
                  tak (- z 1) x y
           else z


define result (tak 18 12 6)

cond
 (equal? result 7)
    "Tak test passed"
 else
    raise "\nTak test failed."

#
# Anonymous version
#
define tak-maker
   lambda ()
      def tak (x y z)
        cond
           (< y x)
              tak
                  tak (- x 1) y z
                  tak (- y 1) z x
                  tak (- z 1) x y
           else z
      the tak


define anon-result 
   (tak-maker) 18 12 6

cond
 (equal? anon-result 7)
    "Anon Tak test passed"
 else
    raise "\nAnon Tak test failed."

