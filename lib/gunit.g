## Copyright 2010 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix : 'http://www.genyris.org/lib/gunit#'
@prefix < "http://www.genyris.org/lang/utilities#"

var suite-errors-messages nil

def :runTests(top)
   :walkDirectoryTree top
   <:format "%s\n" suite-errors-messages

def :walkDirectoryTree (top)
   def runIt(fullpath)
      define results nil
      catch runerrors
         setq results
            apply System!exec
               list "C:\\WINNT\\system32\\cmd.exe" '/c' "genyris" fullpath
      for line in results
         <:format "%s\n" line
      cond
         runerrors
            <:format "%s\n" runerrors
   def includeIt(fullpath)
      catch runerrors
         include fullpath
      cond
         runerrors
            <:format "*** %s\n" runerrors
   def matchit (path filename)
      cond
         (filename (.match 'test\-(.*)\\.g'))
            var fullpath
                "%a/%a" (.format path filename)
            <:format "%a\n" fullpath
            includeIt fullpath
   define f (File(.new top))
   define files (f(.list))
   while files
      define f (left files)
      define path ('%a/%a' (.format top f))
      matchit top f
      cond
         (File!static-is-dir? path)
              :walkDirectoryTree path
      files = (right files)

defmacro :test-suite (suiteheadline &rest block)
   template
      do
         display ("Test Suite: %a\n"(.format ,suiteheadline))
         def :found-error (message)
            setq suite-errors-messages (cons message suite-errors-messages)
         catch test-suite-errors ,@block
         cond
            test-suite-errors
                raise
                    "Exception in %s\n"
                        .format ,suiteheadline
            suite-errors-messages
                for msg in suite-errors-messages
                    print msg
                raise
                    "Failures in %s test suite (%s)\n"
                        .format ,suiteheadline (length suite-errors-messages)

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
