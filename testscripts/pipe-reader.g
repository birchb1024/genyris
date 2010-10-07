#
# Pipes Example - read from a pipe 
#
@prefix task 'http://www.genyris.org/lang/task#'
@prefix sys 'http://www.genyris.org/lang/system#'

print
  "Reading from %s\n"
     .format (nth 1 sys:argv)
define inpipe (Pipe(.open (nth 1 sys:argv)))

define in (inpipe(.input))
var line (in(.getline))
print
   "reader task received: %a"  
      .format line 
assert (equal? 'Hello from the first task' line)

