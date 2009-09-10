## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
#
# How the .valid? method is called by the tagging operator and tag
#

class C ()
    def .valid? (obj)
        > obj 23

# call the validator directly from the class env
assertEqual 
    C (.valid? 34)
    true
assertEqual 
    C (.valid? 21)
    nil


# mark the number 24 as a member of class 'C'
tag C 24      # => ~ 24 # Bignum C
is? 24 C        # => ~ 24 # Bignum

catch error
   is? 22 C
assertEqual error "class C validator error for object 22"
