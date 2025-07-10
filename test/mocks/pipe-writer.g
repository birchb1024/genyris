#
# Pipes Example - to a pipe every 100 msec
#
@ns task 'http://www.genyris.org/lang/task#'
@ns sys 'http://www.genyris.org/lang/system#'

define shared (Pipe(.open (nth 1 sys:argv)))
define out
   shared(.output)
while true
    catch errors
        out(.format 'Hello from task #%s\n' ((task:id) .id))
    sleep 500
