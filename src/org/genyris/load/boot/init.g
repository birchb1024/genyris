## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix g "http://www.genyris.org/lang/syntax#"
@prefix s "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"

class g:Keyword(SimpleSymbol)
tag g:Keyword ^function


# Some aliases
defmacro define (variable valu) (template (defvar ^,variable ,valu))
define function lambda
define var define
defmacro df (name args &rest body)
    template
        defvar ^,name (lambdaq ,args ,@body)

df // (&rest ignore)

defmacro setq (variable valu) (template (set ^,variable ,valu))

defmacro if (test success-result failure-result)
   template
      cond
         ,test ,success-result
         else ,failure-result


defmacro do (&rest expression)
   expression

define else do
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
#
# Load source functions and classes
#
load "org/genyris/load/boot/assert.g"
load "org/genyris/load/boot/pair.g"
load "org/genyris/load/boot/alist.g"
load "org/genyris/load/boot/parse.g"
load "org/genyris/load/boot/object.g"
load "org/genyris/load/boot/listoflines.g"
load "org/genyris/load/boot/file.g"
load "org/genyris/load/boot/util.g"
load "org/genyris/load/boot/version.g"
load "org/genyris/load/boot/set.g"
load "org/genyris/load/boot/triple.g"
load "org/genyris/load/boot/import.g"
load "org/genyris/load/boot/for.g"
load "org/genyris/load/boot/task.g"



