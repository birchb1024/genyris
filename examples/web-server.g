##
##  Simple script to start a static web server.
## Arguments:
## port - e.g. 80
## root path directory name - e.g. "F:\\foo\\bar\\"
##
##
@prefix sys "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"
@prefix task "http://www.genyris.org/lang/task#"

define forever (power 2 62)
define number-threads 10

var port (parse (nth 1 sys:argv))
var rootdir (nth 2 sys:argv)


df httpd-serve (request)
   list ^SERVE-FILE rootdir (request(.getPath)) ^ls

cond
    (equal? (task:id)!name 'main')
        for _ in (range 1 number-threads)
            httpd port @FILE (nth 1 sys:argv) rootdir
        u:format "Serving web pages on port %a from %a\n" port rootdir
        sleep forever
