## Copyright 2010 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix : 'http://www.genyris.org/lib/gunit#'
@prefix < "http://www.genyris.org/lang/utilities#"
@prefix sys "http://www.genyris.org/lang/system#"

define :test-counter 0
define :test-failed-counter 0
define :total-tests-counter 0
define :total-test-failed-counter 0
define :failed-files nil

def :includeIt(fullpath)
   catch (runerrors backtrace)
      include fullpath
   cond
      runerrors
         setq :failed-files (cons fullpath :failed-files)
         <:format "*** %s\n" runerrors
         sys:printBackTrace backtrace
   

def :execFiles(test-files)
   for file in test-files
      <:format "---------%s-----------------------------------------------------\n" file
      setq :test-counter 0
      setq :test-failed-counter 0
      :includeIt file
      setq :total-tests-counter (+ :test-counter :total-tests-counter)
      setq :total-test-failed-counter (+ :test-failed-counter :total-test-failed-counter)

def :runTests(top file-list)
   setq :total-tests-counter 0
   setq :total-test-failed-counter 0
   setq :failed-files nil

   define test-files (:walkDirectoryTree top)

   :execFiles test-files
   :execFiles file-list
   var file-count (+ (length test-files) (length file-list))

   <:format "===========================================================================\n"
   <:format "Total # Test Files: %s\n" file-count
   <:format "Total # gunit Tests: %s\n" :total-tests-counter
   <:format "Total # of failed test files: %s\n" (length  :failed-files)
   for failed in :failed-files
       <:format "    :%s\n" failed
   <:format "Total # gunit tests Failed %s\n"  :total-test-failed-counter

def :found-error(headline test-errors)
   define message
      "Test failed: %s because %a"
         .format headline test-errors
   <:format "%s\n" message
   
def :walkDirectoryTree (top)
   define file-list nil # list of test files to return
   def matchit (fullpath filename)
      filename (.match 'test\-(.*)\\.g')
   define f (File(.new top))
   define files (f(.list))
   for f in files
      define path ('%a/%a' (.format top f))
      cond
         (File!static-is-dir? path)
              setq file-list (append file-list (:walkDirectoryTree path))
         (matchit path f)
            setq file-list (cons path file-list)
   the file-list

defmacro :test (headline &rest block)
   template
      do
         setq :test-counter (+ 1 :test-counter)
         catch test-errors ,@block
         cond
            test-errors
               setq :test-failed-counter (+ 1 :test-failed-counter)
               :found-error ,headline test-errors
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

defmacro :assertNil (expression)
  template
     cond
        (,expression)
            raise (list "assertNil failed on expression: " ^,expression)

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
