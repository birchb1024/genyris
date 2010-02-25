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
### Examples of constructors, traditional and modern.
###

#
#  A class which does it's own construction but checks nothing
#
class BasicPerson ()
    def .new (name age)
      (tag BasicPerson (dict))
        define .name name
        define .age age
        .self

assert
   and
     is-instance? (BasicPerson!new "w" 2) BasicPerson
     equal? 2
       (BasicPerson!new "w" 2).age
#
#  A class which uses the .new in Object, and adds type checking
#
class PersonTraditional (Object)
   def .init((name=String) (age = Bignum))
      define .name name
      define .age age


class PersonModern ()
   def .valid?(obj)
      is? obj!name String
      is? obj!age Bignum
   def .new(name age)
      (dict)                             # create the object
         define .name name               # add the data
         define .age age
         is? .self PersonModern           # check type now
         tag PersonModern .self           # ok, so add the class
         .self

define fred (PersonModern!new "fred" 23)

assert
   and
     is-instance? fred PersonModern
     equal? fred!age 23
