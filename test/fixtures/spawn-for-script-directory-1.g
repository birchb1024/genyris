@ns sys "http://www.genyris.org/lang/system#"
@ns task "http://www.genyris.org/lang/task#"

def prepend-home (relative-path)
    System!HOME (.+ '/' relative-path)
var response (graph) # for run un-spawned
cond
  (> (length sys:argv) 2)
     setq response (nth 2 sys:argv) # if spawned
var id (nth 1 sys:argv)
assertEqual sys:script-directory (prepend-home 'test/fixtures')
assert (member? "." sys:path)
task:synchronized response
  response(.put (intern id) ^HOME System!HOME)
  response(.put (intern id) ^sys:path sys:path)
  response(.put (intern id) ^sys:script-directory sys:script-directory)
  response(.put (intern id) ^sys:argv sys:argv)
  response(.put (intern id) (intern 'A') (equal? sys:script-directory (prepend-home 'test/fixtures')))
  response(.put (intern id) (intern 'B') (not (null? (member? "." sys:path))))
  response(.put (intern id) (intern 'C') true)
