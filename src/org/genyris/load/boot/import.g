## Copyright 2009 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix sys "http://www.genyris.org/lang/system#"

define sys:path ^(".")
define sys:modules (graph)

class Module

defmacro import (moduleName)
   # macro ensures module binding is create in the scope of caller
   define themodule (import-aux moduleName)
   if themodule
       template
           define ,moduleName ,themodule
       '%s already defined'
            .format moduleName

def sys:module-defined? (moduleName)
    define answer
        sys:modules(.select moduleName ^sys:hasModule nil)
    answer(.asTriples)

def import-aux (moduleName)
    define existing (sys:module-defined? moduleName)
    cond
        existing
            (left existing)(.object)
        else
            define path (sys:search-path ((asString moduleName)(.+ '.g')))
            if path
                 sys:mk-module moduleName path
                 raise ("Unable to locate import "(.+ (asString moduleName)))

def sys:search-path(fileName)
    define tmp sys:path
    define result nil
    while tmp
        define file-path ((left tmp)(.+ "/" fileName))
        cond
            ((File.exists) file-path)
                  setq tmp nil
                  setq result file-path
            else
                  setq tmp (right tmp)
    result

def sys:mk-module (moduleName path)
     define themodule (dict (.name = moduleName)(.filename = path))
     themodule (sys:import path)
     tag Module themodule
     sys:modules
        .add (triple moduleName ^sys:hasModule themodule)
     themodule

defmacro reload (moduleName)
   # macro ensures module binding is create in the scope of caller
   define notNew (sys:module-defined? moduleName)
   cond
    (notNew)
        define oldmodule ((left notNew)(.object))
        sys:modules
            .remove (left notNew)
        define themodule (sys:mk-module moduleName(oldmodule.filename))
        template
            define ,moduleName ,themodule
    else
         raise ('%s was not loaded yet'(.format moduleName))

def include((filename = String))
   cond
      (equal? '/' (filename(.slice 0 0)))
           sys:include filename
      else
           include-relative filename

def include-relative(filename)
   var path
     sys:search-path filename
   cond
      path
        sys:include path
      else
        raise
           'include: could not locate %s' (.format filename)
