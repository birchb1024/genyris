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
# change to
defmacro setq (var valu) (template (define ,var ,valu))
#
# 2579718 set not working in methods
#
class C(Object)
   def .init()
      define .v 99
   def .f()
      set ^.v 34
define c (C(.new nil))
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
