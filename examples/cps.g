###
###  Explorations in Continuation Passing Style and Trampolining
###


defmacro printlistm(alist)
  # A recursive macro that prints out each element in the list given to it.
  # This is unusual because although it is recursive, it does not increase stack depth.
  # The next evaluation is done in the context of the caller. This is an example of Trampolining.
   cond
      (null? alist)
         ^^nil
      else
         print (car alist)
         template            # return the following code snippet to be re-evaluated in the caller
            printlistm ,(cdr alist)

def printlist (al)
  # a wrapper function which evaluates it's argument and passes to the recursive macro.
  eval
    template
      printlistm ,al

printlist ^(1 2 3 4 5)
