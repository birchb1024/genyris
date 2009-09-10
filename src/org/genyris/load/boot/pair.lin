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
         result = (some-function (left some-list))
         some-list = (right some-list)
   result


def member? (item list)
   cond
      (null? list) nil
      (equal? (left list) item) list
      else
          member? item (right list)


Pair
   defmacro .each (args &rest body)
       template
          loop-left .self (lambda (,(left args)) ,@body)


