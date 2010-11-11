## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
include "lib/classify.g"

def between (low it high)
   and
      < it high
      > it low

class Person ()
   def .valid? (obj)
      obj
         bound? ^.age

class Kid (Person)
   def .valid? (obj)
      obj
         < .age 15

class Senior (Person)
   def .valid? (obj)
      obj
         between 60 .age 120

class Boomer (Person)
   def .valid? (obj)
      obj
         between 45 .age 60

class Hippie (Boomer)
   def .valid? (obj)
      obj
         bound? ^.bong

define noel
  dict
     .name = "Noel"
     .age  = 49
     .bong = true

define kevin
  dict
     .name = "Kevin"
     .age  = 49

classify Person noel
classify Person kevin

assert
  and
    is-instance? noel Hippie
    is-instance? kevin Boomer
