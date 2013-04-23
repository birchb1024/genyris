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
   

   
   