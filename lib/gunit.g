## Copyright 2010 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix : 'http://www.genyris.org/lang/gunit#'

defmacro :test-suite (suiteheadline &rest block)
   template
      do
         display ("Test Suite: %a\n"(.format ,suiteheadline))
         var suite-error-count 
         def :found-error(message)
            display ":fond-error\n"
            display message
            
         catch test-suite-errors ,@block
         cond
            test-suite-errors
                raise
                    "Errors in %s\n"
                        .format ,suiteheadline

defmacro :test (headline &rest block)
   template
      do
         catch test-errors ,@block
         cond
            test-errors
               define message
                  "Test failed: %s because %a"
                     .format ,headline test-errors
               :found-error message             
            else
               display
                  "Test passed: %s\n"
                     .format ,headline
#
#  Asserts
#
defmacro :assert (expression)
  template
     cond
        (null? ,expression)
            raise (list "assert failed on expression: " ^,expression)

defmacro :assertEqual (a b)
  template
     cond
        (equal? ,a ,b)
        else
          raise (list "assert failed on expression: " ^,a ^,b "values:" ,a ,b)

defmacro :assertException (exceptn &rest block)
  template
     do
        catch exception-error  ,@block
        cond
            exception-error nil
            else
                raise (list "assertException not raised on expression: " ^,block)
