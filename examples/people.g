## Copyright 2014 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

# Uses Friend Of A Friend class names.
@prefix foaf 'http://xmlns.com/foaf/0.1/'

include "lib/classify.g"

def between (low it high)
   and
      < it high
      > it low


class foaf:name

class foaf:Person ()
  # Any object with a foaf:age is a foaf:Person
  def .valid? (obj)
      obj
         bound? ^.foaf:age 

class Kid (foaf:Person)
  # Kids are younger than 15
  def .valid? (obj)
    obj
       < .foaf:age 15

class Senior (foaf:Person)
  # Older than 60 makes a Senior
  def .valid? (obj)
    obj
       between 60 .foaf:age 120

class Boomer (foaf:Person)
  # Baby boomers
  def .valid? (obj)
    obj
       between 45 .foaf:age 60

class Hippie (Boomer)
  # A baby boomer who owns a bong
  def .valid? (obj)
    obj
       and
            bound? ^.bong
            .bong

# Let's create some data and assign to variables...
define neil
  dict
    .foaf:name = "Neil Wheedon Watkins Pye"
    .foaf:age  = 49
    .bong = true

define vyvyan
  dict
     .foaf:name = "Vyvyan"
     .foaf:age  = 49

define rick
  dict
     .name = (tag foaf:name "Rick") # not legal in RDF

# Classify the data..
classify foaf:Person neil
classify foaf:Person vyvyan
classify foaf:Person rick

# What classes did we get?
assert
  and
    is-instance? neil Hippie
    is-instance? vyvyan Boomer
    not
      is-instance? rick foaf:Person
    is-instance? rick!name foaf:name
