#
# Script to locate executables on the windows
# system path.
#
@prefix u "http://www.genyris.org/lang/utilities#"

def which(exe)
    var result nil
    for dir in ((System!getenv 'path')(.split ';'))
       catch errors
          cond
             (File(.exists ("%a\\%a" (.format dir exe))))
                 setq result (cons dir result)
       cond
           errors
                stderr (.format "which error: %a\n" errors)
       result
