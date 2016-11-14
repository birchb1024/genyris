#
# A very simple shell
#
@prefix sys "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"

define OS (os(.getProperties))!|os.name|

#
# Linux:
df sys:procedure-missing(&rest args)
   # most elementary version returns list of lines
   apply os!exec args

df sys:procedure-missing(&rest args)
   # simple quoted version 
   for line in (left (apply os!exec args))
         u:format "%a\n" line
       
#
# Windows:
def sys:procedure-missing(&rest args)
   left
      apply os!exec (append ^(cmd /c) args)
   
# basic version with no error handling
def sys:procedure-missing(&rest args)
   left
      apply os!exec (append ^(cmd /c) args)
      
define my-dir 'test\\fixtures\\gunit'
cond
    (equal? OS 'Windows')
        assertEqual
           dir ^/b my-dir
           ^('test-file-one.g' 'test-file-three.g' 'test-file-two.g')


def sys:procedure-missing(&rest args)
    # with error trapping and printing
    define errors nil
    define results ^(nil)
    cond
        (equal? OS 'Linux')
           catch errors
              setq results
                 apply os!exec (append ^(bash -c) (list args))
              results
        (equal? OS 'Windows')
           catch errors
              setq results
                 apply os!exec (append ^(cmd /c) (list args))
              for line in (left results)
                 u:format "%a\n" line
        else
            u:format "Unknown OS %s\n" OS
    cond
        errors
            for line in (left errors)
                u:format "%a\n" line
            raise (right errors)
    results
   
