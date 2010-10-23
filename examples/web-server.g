##
##  Simple script to start a static web server.
## Arguments:
## port - e.g. 80
## root path directory name - e.g. "F:\\foo\\bar\\"
##
##
@prefix sys "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"
print sys:argv
cond
   (not (equal? (length sys:argv) 3))
       raise "Usage: web-server.g <port> <root directory>"

var port (nth 1 sys:argv)
var rootdir (nth 2 sys:argv)
u:format "Serving web pages on port %a from %a%n" port rootdir
httpd port 'test/mocks/www-static.g' rootdir

