## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix foaf 'http://xmlns.com/foaf/0.1/'

include "lib/classify.g"

def between (low it high)
   and
      < it high
      > it low

define neil
  dict
     .foaf:name = "Neil Wheedon Watkins Pye"
     .foaf:age  = 49
     .bong = true

define vyvyan
  dict
     .foaf:name = "Vyvyan"
     .foaf:age  = 49

class foaf:name

define rick
  dict
     .name = (tag foaf:name "Rick") # not legal in RDF

class foaf:Person ()
   def .valid? (obj)
      # simple type inference from foaf:age to foaf:Person
      obj
         bound? ^.foaf:age 

class Kid (foaf:Person)
   def .valid? (obj)
      obj
         < .foaf:age 15

class Senior (foaf:Person)
   def .valid? (obj)
      obj
         between 60 .foaf:age 120

class Boomer (foaf:Person)
   def .valid? (obj)
      obj
         between 45 .foaf:age 60

class Hippie (Boomer)
   def .valid? (obj)
      obj
         bound? ^.bong


classify foaf:Person neil
classify foaf:Person vyvyan

assert
  and
    is-instance? neil Hippie
    is-instance? vyvyan Boomer
    is-instance? rick!name foaf:name
