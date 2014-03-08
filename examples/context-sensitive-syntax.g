## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
###
### Examples of context sensitive macros (also object-oriented macros)
###

#------------------------------------------------------
#
#  Define a macro in a private closure
#
def make-env ()
  defmacro if (test success-result failure-result)
    template
       cond
          $test (list $success-result "env-OK")
          true (list $failure-result "env-Fail")
  def run-example ()
      if true 11 22
  the run-example

assert 
    equal? ((make-env)) (list 11 "env-OK")

#------------------------------------------------------
#
# simple macro defined by an un-classed object
#
define context (dict)

context
  # define a macro in the environment of this object
  defmacro .if (test success-result failure-result)
    template
       cond
          $test (list $success-result "OK")
          true (list $failure-result "Fail")

assert
   equal?
      context
           .if nil "A" "B"            # call the macro.
      list "B" "Fail"

#------------------------------------------------------
#
# Now a macro belonging to a class.
#
class MySuperClass ()
  defmacro .if (test success-result failure-result)
    template
       cond
          $test (list $success-result "my-OK")
          true (list $failure-result "my-Fail")


class MyClass (MySuperClass)       # sub-class it


define my-instance                 # create an instance
   tag MyClass (dict)

assert
    equal?
        list "B" "my-Fail"
        my-instance              # use the instance as an execution environment
           .if nil "A" "B"


