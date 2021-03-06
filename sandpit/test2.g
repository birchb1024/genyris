###
###  Use of lambda to define a function
###
defvar ^last
  lambda (x)
    cond
      (eq nil (cdr x))
         car x
      else
         last (cdr x)


cond
   (equal (last ^(1 2 3 4 5 6)) 6) =            # list line continuation
      "PASSED"
   else
      ~ "FAILED"                                # line continuation with ~ syntax

