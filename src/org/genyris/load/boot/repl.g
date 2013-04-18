@prefix u "http://www.genyris.org/lang/utilities#"
@prefix ver "http://www.genyris.org/lang/version#"
@prefix sys "http://www.genyris.org/lang/system#"
@prefix java 'http://www.genyris.org/lang/java#'

import versioninfo

def sys:print-classnames(obj)
    define klasses (use obj (the .classes))
    while klasses
        u:format '%a ' ((left klasses).classname)
        setq klasses (cdr klasses)
 
def sys:printBackTrace(bt)
   # setq bt (cdr (cdr bt))
   while bt
       print (left bt)
       setq bt (cdr bt)

def sys:read-eval-print-loop()
   u:format "*** Welcome %a, %a version %a, home %a is listening...%n"
        (os(.getProperties)).|user.name|
        versioninfo.title
        versioninfo.version
        System.HOME
   define looping true          
   while looping
       define bt nil
       catch (errors bt)
           define expression (read)
           define result (eval expression)
       cond
           errors
               u:format "*** Error - %s\n" errors
               sys:printBackTrace bt
               setq bt nil             
           else
                cond
                    (equal? result EOF)
                        setq looping nil
                u:format '%s # ' result
                sys:print-classnames result
                u:format '\n'
