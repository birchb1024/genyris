@ns sys "http://www.genyris.org/lang/system#"
@ns task "http://www.genyris.org/lang/task#"

var forever (power 2 62)
def prepend-home (relative-path)
  System!HOME (.+ '/' relative-path)

print (list @FILE @LINE System!HOME)
print (list @FILE @LINE sys:argv)
print (list @FILE @LINE sys:script-directory)
print (list @FILE @LINE (' '(.join sys:path)))
print (list @FILE @LINE (prepend-home 'test/acceptance'))
print (list @FILE @LINE (sys:search-path '../fixtures/spawn-for-script-directory-1.g'))
print (list @FILE @LINE (File!static-abs-path (prepend-home 'test/fixtures/spawn-for-script-directory-1.g')))

assertEqual sys:script-directory (prepend-home 'test/acceptance')
assert (member? "." sys:path)

# Test include does not chnage script-directory
include '../fixtures/child-for-script-directory-1.g'
assertEqual sys:script-directory (prepend-home 'test/acceptance')
assert (member? "." sys:path)

# Tests for spawned tasks
var child-response (graph)

# Test to see if script directory is set up in spawned tasks...
spawn '../fixtures/spawn-for-script-directory-1.g' 'alpha' child-response
sleep 1000
task:synchronized child-response
  for T in  (child-response(.asTriples))
    print (list @FILE @LINE T)
  for P in  ^('A' 'B' 'C')
    assert (child-response(.get (intern 'alpha') (intern P)))

# Test to see if script directory is set up in spawned tasks...
task:spawn ('%a/../fixtures/spawn-for-script-directory-1.g'(.format sys:script-directory)) 'bravo' child-response
sleep 1000
task:synchronized child-response
  for T in  (child-response(.asTriples))
    print (list @FILE @LINE T)
  for P in  ^('A' 'B' 'C')
    assert (child-response(.get (intern 'alpha') (intern P)))

sys:add-parent-to-path
task:spawn (sys:search-path '../fixtures/spawn-for-script-directory-1.g') 'bravo' child-response
sleep 1000
task:synchronized child-response
  for T in  (child-response(.asTriples))
    print (list @FILE @LINE T)
  for P in  ^('A' 'B' 'C')
    assert (child-response(.get (intern 'alpha') (intern P)))
