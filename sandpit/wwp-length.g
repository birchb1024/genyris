@prefix l "http://www.measures.org/length#"

class l:Inches(Bignum)
   def .toMeters()
      * .self 0.0254

define a-foot
   tag l:Inches 12

assert
   equal?
       a-foot (.toMeters)
       0.3048

class l:l.Length()
   def .toMeters()
      raise "Oops - you invoked an abstract class."

class l.Inches(l.Length)
   def .toMeters()
      tag Meters (* .self 0.0254)

class l.Meters(l.Length)
   def .toMeters() .self

l.Length
   def .add(other)
      tag l.Meters
         + (.toMeters)
            other (.toMeters)


define a-meter
   tag l.Meters 1

define a-foot (tag l.Inches 12)

assert
   equal?
      a-foot (.add a-meter)
      1.3048
assert
   is-instance? (a-foot (.add a-meter)) l.Meters