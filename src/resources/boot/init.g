## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@ns syn "http://www.genyris.org/lang/syntax#"
@ns sys "http://www.genyris.org/lang/system#"
@ns u "http://www.genyris.org/lang/utilities#"
@ns date "http://www.genyris.org/lang/date#"
@ns math "http://www.genyris.org/lang/math#"

defvar ^sys:path ^('.')
class syn:Keyword(SimpleSymbol)
tag syn:Keyword ^function

# nil is in a class of it's own
class NilSymbol(Symbol)
tag NilSymbol ^nil

# Some aliases
defmacro define (variable valu) (template (defvar ^$variable $valu))

define include sys:include
define var define
defmacro df (name args &rest body)
    template
        defvar ^$name (lambdaq $args $@body)

defmacro defmethod (name args &rest body)
    # this macro binds 'this' inside a function.
    template
        def $name $args (defvar ^this .self) $@body

#df // (&rest ignore)

df data (&rest args) args

defmacro setq (variable valu) (template (set ^$variable $valu))

defmacro if (test success-result failure-result)
   template
      cond
         $test $success-result
         else $failure-result


defmacro do (&rest expression) 
   template
     (lambda () $@expression)

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
       setq $varname (- $varname 1)

defmacro ++ (varname)
   template
       setq $varname (+ $varname 1)

System
   define .HOME
      File!static-abs-path
         (os!getenv 'GENYRIS_HOME')
            .replace '\\' '/'
   define .LIBS 
       "%a/lib" (.format (System.HOME))

def *shutdown-hook* ()

var math:pi 3.1415926535897932384626433832795028841971

#
# Load source functions and classes
#
load "boot/java.g"
load "boot/assert.g"
load "boot/pair.g"
load "boot/alist.g"
load "boot/parse.g"
load "boot/object.g"
load "boot/iterator.g"
load "boot/range.g"
load "boot/listoflines.g"
load "boot/file.g"
load "boot/util.g"
load "boot/set.g"
load "boot/triple.g"
load "boot/for.g"
load "boot/import.g"

setq sys:path (append (list System!LIBS System!HOME) sys:path)

load "boot/task.g"
load "boot/process.g"
load "boot/repl.g"
load "boot/HttpRequest.g"
load "boot/os.g"

import Base64

def now() (os!ticks)

def sys:self-test()
    include 'test/acceptance/suite.g'
