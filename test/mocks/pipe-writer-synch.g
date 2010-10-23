#
# Pipes Example - to a pipe every 100 msec
#
@prefix task 'http://www.genyris.org/lang/task#'
@prefix sys 'http://www.genyris.org/lang/system#'

define shared (Pipe(.open (nth 1 sys:argv)))
define out
   shared(.output)
while true
    task:synchronized shared
       catch errors
          out(.format 'Hello from task.\n')
    sleep 500
