## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  Asserts
#
defmacro assert (expression)
  cond
    (is-instance? expression PairSource)
      template
         cond
            (null? $expression)
                raise ("%a:%a macro assert failed on: %s value: %s"(.format $(expression!filename) $(expression!line-number) ^$expression $expression))
    else
      template
         cond
            (null? $expression)
                raise ("macro assert failed on: %s value: %s"(.format ^$expression $expression))

defmacro assertEqual (a b)
  cond
    (is-instance? a PairSource)
      template
         cond
            (not (equal? $a $b))
              raise ("%a:%a macro assertEqual failed on: %s %s values: %s %s"(.format $(a!filename) $(a!line-number) ^$a ^$b $a $b))
    (is-instance? b PairSource)
      template
         cond
            (not (equal? $a $b))
              raise ("%a:%a macro assertEqual failed on: %s %s values: %s %s"(.format $(b!filename) $(b!line-number) ^$a ^$b $a $b))
    else
      template
         cond
            (not (equal? $a $b))
              raise ("macro assertEqual failed on: %s %s values: %s %s"(.format ^$a ^$b $a $b))
