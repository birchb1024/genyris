## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  A base class for traditional construction activities
#
class Object (Dictionary)
   # generic .new method
   # expects every class to have an .init method
   def .new(&rest args)
      (tag .self (dict))
         apply .init args
         .self

   # NOOP .init method
   def .init(&rest args)
      .self
