#
# Wrappers for threads
#
class TaskList
var oldps ps
def ps ()
  tag TaskList (oldps)
  
def ls()
   File(.static-list-dir '.')
   
defmacro perf (&rest body)
   template
      do
        define start ((os.ticks))
        ,@body
        - ((os.ticks)) start