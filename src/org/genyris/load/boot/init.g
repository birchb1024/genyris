## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix g "http://www.genyris.org/lang/syntax#"
@prefix s "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"
@prefix date "http://www.genyris.org/lang/date#"

class g:Keyword(SimpleSymbol)
tag g:Keyword ^function

# nil is in a class of it's own
class NilSymbol(Symbol)
tag NilSymbol ^nil

# Some aliases
defmacro define (variable valu) (template (defvar ^,variable ,valu))

define include s:include
define function lambda
define var define
defmacro df (name args &rest body)
    template
        defvar ^,name (lambdaq ,args ,@body)

defmacro defmethod (name args &rest body)
    # this macro binds 'this' inside a function.
    template
        def ,name ,args (defvar ^this .self) ,@body

df // (&rest ignore)

defmacro setq (variable valu) (template (set ^,variable ,valu))

defmacro if (test success-result failure-result)
   template
      cond
         ,test ,success-result
         else ,failure-result


defmacro do (&rest expression)
   expression

def else()
   raise "Hanging else"
define then do

def null? (sexp)
   cond
      sexp nil
      else true

def abs (x)
  cond
    (< x 0) (- x)
    else x

defmacro -- (varname)
   template
       setq ,varname (- ,varname 1)

defmacro ++ (varname)
   template
       setq ,varname (+ ,varname 1)

define GENYRIS_HOME ((System!getenv).GENYRIS_HOME)
define GENYRIS_LIBS ("%a/lib"(.format GENYRIS_HOME))
#
# Load source functions and classes
#
load "org/genyris/load/boot/java.g"
load "org/genyris/load/boot/assert.g"
load "org/genyris/load/boot/pair.g"
load "org/genyris/load/boot/alist.g"
load "org/genyris/load/boot/parse.g"
load "org/genyris/load/boot/object.g"
load "org/genyris/load/boot/iterator.g"
load "org/genyris/load/boot/range.g"
load "org/genyris/load/boot/listoflines.g"
load "org/genyris/load/boot/file.g"
load "org/genyris/load/boot/util.g"
load "org/genyris/load/boot/set.g"
load "org/genyris/load/boot/triple.g"
load "org/genyris/load/boot/import.g"
s:path = (cons GENYRIS_LIBS s:path)
load "org/genyris/load/boot/for.g"
load "org/genyris/load/boot/task.g"

import Base64

class ShortDateTimeString(String)
def format-date(epoch format)
   tag ShortDateTimeString
      date:format-date epoch format
def now() (System!ticks)

class Calendar
def calendar(epochmilliseconds)
    tag Calendar (date:calendar  epochmilliseconds)

def s:self-test()
    include 'test/acceptance/suite.g'
