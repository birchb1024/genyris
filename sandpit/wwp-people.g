## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
load "lib/classify.g"

@prefix p "http://www.humans.org/people#"

def between (low it high)
   and
      < it high
      > it low

class p:Person ()
   def .valid? (obj)
      obj
         bound? ^.age

class p:Kid (p:Person)
   def .valid? (obj)
      obj
         < .age 15

class p:Senior (p:Person)
   def .valid? (obj)
      obj
         between 60 .age 120

class p:Boomer (p:Person)
   def .valid? (obj)
      obj
         between 45 .age 60

class p:Hippie (p:Boomer)
   def .valid? (obj)
      obj
         bound? ^.bong

var noel
  dict
     .name = "Noel"
     .age = 49
     .has-bong = true

classify p:Person noel

assert
  is-instance? noel p:Hippie

noel
