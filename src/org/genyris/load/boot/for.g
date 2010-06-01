#
# Simple macro to provide python-esque looping syntax.
# Objects must have a .mkIterator method.
#
# Example:
#   for f in ^(a b c d)
#      print f
@prefix sys "http://www.genyris.org/lang/system#"

defmacro for (varname keyword container &rest body)
  cond ((not (equal? keyword ^in)) (raise 'syntax error: for was expecting in'))
  define lexp (gensym 'lexp')
  template
   (function (,lexp)  (while (not (eq? (define ,varname (,lexp)) ^sys:StopIteration)) ,@body)) (,container(.mkIterator))

