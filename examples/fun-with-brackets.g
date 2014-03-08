## Copyright 2009 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
#
# As an array lookup index
#
def squareBracket(index)
    nth index .self

var array ^(1 2 3 4 5)
assert 
    equal? 
        array[3]
        4

##--------------------------
# Like Python list declarations
#

def squareBracket(&rest args)
    args

assert (equal? [1 2 3] (list 1 2 3) )
##--------------------------
# Dictionary declarations
#

defmacro curlyBracket(&rest args)
    template
         dict $@args
{(.a = 23) (.b = 45)}

##--------------------------
# For polymorphic types
#
def squareBracket(type)
    define .T type

class C(Object)[String]
    def .method() (list 1231 .T)

define c (C(.new))
assert
   equal?
      c(.method)
      list 1231 String

