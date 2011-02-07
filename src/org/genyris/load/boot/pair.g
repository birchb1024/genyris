## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  Methods and functions for lists.
#
define left car
define right cdr
def cadr (x) (nth 1 x)
def cadar (x) (car (cdr (car x)))
def caar (x) (car (car x))

def map-left (some-list some-function)
   cond
      some-list
         cons
            some-function (left some-list)
            map-left (right some-list) some-function

def loop-left (some-list some-function)
   var result nil
   while some-list
         setq result (some-function (left some-list))
         setq some-list (right some-list)
   result


def member? (item list)
   cond
      (null? list) nil
      (equal? (left list) item) list
      else
          member? item (right list)

def append (list1 list2)
    cond
        (null? list1) list2
        else
           cons
              left list1
              append (right list1) list2
Pair
   def .nth (x) (nth x .self)
   # example: 
   # (^(a b c)(.nth 1))
   defmacro .each (args &rest body)
       template
          loop-left .self (function (,(left args)) ,@body)
   # Example:
   # ^(1 2 3 4)
   #   .each (f)
   #      print f


def beginsWith?(this other)
   cond
       this
           and
                other
                equal? (left this) (left other)
                beginsWith? (right this)(right other)
       else
            true
