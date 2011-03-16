#
# A very simple shell
#
@prefix sys "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"

def sys:procedure-missing(&rest args)
   left
      apply System!exec (append ^(cmd /c) args)
   
# basic version with no error handling
def sys:procedure-missing(&rest args)
   left
      apply System!exec (append ^(cmd /c) args)
      
define my-dir 'test\\fixtures\\gunit'
assertEqual
   dir ^/b my-dir
   ^('test-file-one.g' 'test-file-three.g' 'test-file-two.g')

def sys:procedure-missing(&rest args)
   # with error trapping and printing
   catch errors
      var results
         apply System!exec (append ^(cmd /c) args)
      for line in (left results)
         u:format "%a\n" line
   cond
      errors
           for line in (left errors)
              u:format "%a\n" line
           raise (right errors)

   
