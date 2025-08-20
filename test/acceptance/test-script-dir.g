@ns sys "http://www.genyris.org/lang/system#"

def prepend-home (relative-path)
  System!HOME (.+ '/' relative-path)

print (list @LINE System!HOME)
print (list @LINE sys:argv)
print (list @LINE sys:script-directory)
print (list @LINE (prepend-home 'test/acceptance'))
assertEqual sys:script-directory (prepend-home 'test/acceptance')
assert (member? "." sys:path)

