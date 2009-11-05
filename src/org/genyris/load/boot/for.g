#
# Simple macro to provide python-esque looping syntax.
# Only works for lists, so far.
#
# Example:
#   for f in ^(a b c d)
#      print f

defmacro for (varname keyword container &rest body)
  cond ((not (equal? keyword ^in)) (raise 'syntax error: for was expecting in'))
  template
   (function (lexp) (while lexp (define ,varname (left lexp)) (setq lexp (right lexp)) ,@body)) ,container

