##
## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
###
### Metaclass programming.
###

# Alternative definition of the inbuilt 'class' function
# does not fix up references in superclasses .subclasses yet..

defmacro myclass (name superclasses &rest body)
   define klass (dict)
   klass
      define .classname name
      tag StandardClass .self
      define .subclasses nil
      define .superclasses
         map-left superclasses symbol-value
      add-to-subclasses superclasses klass
   template
      (define $name $klass) $@body

def add-to-subclasses (supers klass)
   nil  # not coded

myclass Bread (Thing)
  def .method1() "Hello1"
  def .method2() "Hello2"

##
## Example of a meta-class, new classes are made with ".new"
##
class MyMetaClass()
    defmacro .new (name superclasses &rest body)
      define klass (dict)
      klass
         define .classname name
         tag MyMetaClass .self 
         define .subclasses nil
         define .superclasses
            map-left superclasses symbol-value
         add-to-subclasses superclasses klass
         eval body
         def .new()
            define .wheat nil
      template
         define $name $klass

## Create a new class
(MyMetaClass.new) Rye(Thing)
   def .dark?() true




