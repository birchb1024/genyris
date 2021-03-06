## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
###
### Examples of encapsulation via information hiding.
###
@prefix sys "http://www.genyris.org/lang/system#"

def sys:procedure-missing(&rest args)
  raise ("procedure missing: %s"(.format args))

##
## Example of an object created with private field.
## The "age" slot cannot be seen since it is
## within the .init closure. .getAge and .setage capture
## the closure.
##
class Person ()
   def .new()
      tag .self
         (dict)
            define age 25
            def .getAge() age
            def .setAge(val) (set ^age val)
            .self

define p
   Person(.new)
assertEqual 25 (p (.getAge))
assertEqual 26 (p (.setAge 26))

##
## Example of a class with a private method.
##
class Foo ()
    def privateFunc() "I am private. You cannot call me directly"
    def .publicFunc()
       list (privateFunc) "called by .publicFunc"

define f (tag Foo (dict))
assertEqual 
    f (.publicFunc)
    list "I am private. You cannot call me directly" "called by .publicFunc"
catch error
   f (privateFunc) # = error
assert error

