## Copyright 2009 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

class Inches(Bignum)
   def .toMeters()
      * .self 0.0254

# syntactic sugar
def inches()
   tag Inches .self

# create some data
define a-foot
   12(inches)

assert
   equal?
       a-foot (.toMeters)
       0.3048

# Now add a super-class and another Unit...
class Length()
   def .toMeters()
      raise "Oops - you called an abstract class."

class Inches(Length)
   def .toMeters()
      tag Meters (* .self 0.0254)

class Meters(Length)
   def .toMeters() .self
def meter()
   tag Meters .self

Length
   def .add(other)
      tag Meters
         + (.toMeters)
            other (.toMeters)


# create some data
define a-meter 
   1(meter)

define a-foot
   12(inches)

assert
   equal?
      a-foot (.add a-meter)
      1.3048
assert
   is-instance? (a-foot (.add a-meter)) Meters