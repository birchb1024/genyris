#
# 001 @prefix - prefixes bleed over from include into the top-level sessions...
#


#
# 002 &rest does not work with an empty argument list
#
def func(&rest args)  args
assert 
   null? (func)  # *** Error: Too few arguments supplied to proc: func

#
#  003 setq not working when embedded in functions
# changed
#defmacro setq (var valu) (template (define ,var ,valu))
#
# 2579718 set not working in methods
#
class C(Object)
   def .init()
      define .v 99
   def .f()
      set ^.v 34
define c (C(.new))
c(.f)
assert (equal? 34 (c.v))
#
# Defect with QualifiedSymbols not being understood
#
defvar (intern "http://foo/bar#quux") 34
assert (equal? (eval (intern "http://foo/bar#quux")) 34)
#
#  3009789 equal? not working for dynamic symbols.
#
assert (equal? ^.a ^.a)
assert (not (equal? ^.a ^.b))

#
# Issue 
#
catch error
   catch 12
       "Should throw here."
assert error


#
# Issue 54
#
def ma(x) x
assertEqual 
   use ma .source
   ^(lambda (x) x)   
defmacro mac(x) x
assertEqual 
   use mac .source
   ^(lambdam (x) x)  
df maf(x) x
assertEqual 
   use maf .source
   ^(lambdaq (x) x)
   
#
# Issue 7:    Handle empty &rest argument lists better.
# https://code.google.com/p/genyris/issues/detail?id=7
#
catch syntax-error
  lambda (a &rest)
assert syntax-error

catch syntax-error
  lambdaq (a &rest)
assert syntax-error

catch syntax-error
  lambdam (a &rest)
assert syntax-error

catch syntax-error
  lambda (a &rest = String)
assert syntax-error

catch syntax-error
  lambdaq (a &rest = String)
assert syntax-error

catch syntax-error
  lambdam (a &rest = String)
assert syntax-error

catch syntax-error
  def fx(a1 &rest)
assert syntax-error

catch syntax-error
  def fx(&rest)
assert syntax-error

catch syntax-error
  defmacro mx(a1 &rest)
assert syntax-error

catch syntax-error
  defmacro mx(&rest)
assert syntax-error

catch syntax-error
  df mx(a1 &rest) a1
  mx 1
assert syntax-error

catch syntax-error
  df mx(&rest)
  mx
assert syntax-error

catch syntax-error
  def fx(a1 &rest = String)
assert syntax-error

catch syntax-error
  def fx(&rest = String)
assert syntax-error

catch syntax-error
  defmacro mx(a1 &rest = String)
assert syntax-error

catch syntax-error
  defmacro mx(&rest = String)
assert syntax-error

catch syntax-error
  df mx(a1 &rest = String) a1
  mx 1
assert syntax-error

catch syntax-error
  df mx(&rest = String)
  mx
assert syntax-error

#
# Issue 63:    Biscuit is Missing builtin class - fatal!
#
@prefix sys "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"
             
defmacro sys:procedure-missing(&rest args) 
   quote (list 123 456)
assert (a-missing-function)

defmacro sys:procedure-missing(&rest args) ^(unboundsymbol)

assertEqual Biscuit (left (some-undefined-function)!classes)
 
