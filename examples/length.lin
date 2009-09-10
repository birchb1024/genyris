## Copyright 2009 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

class Inches(Bignum)
   def .toMeters()
      * .self 0.0254

define a-foot
   tag Inches 12

assert
   equal?
       a-foot (.toMeters)
       0.3048

class Length()
   def .toMeters()
      raise "Oops - you invoked an abstract class."

class Inches(Length)
   def .toMeters()
      tag Meters (* .self 0.0254)

class Meters(Length)
   def .toMeters() .self

Length
   def .add(other)
      tag Meters
         + (.toMeters)
            other (.toMeters)


define a-meter
   tag Meters 1

define a-foot (tag Inches 12)

assert
   equal?
      a-foot (.add a-meter)
      1.3048
assert
   is-instance? (a-foot (.add a-meter)) Meters