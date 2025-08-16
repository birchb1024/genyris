@ns sys "http://www.genyris.org/lang/system#"

def prepend-home (relative-path)
  System!HOME (.+ '/' relative-path)

print System!HOME
print sys:argv
print sys:script-directory
assertEqual sys:script-directory (prepend-home 'test/acceptance')
assert (member? "." sys:path)
assert (member? sys:script-directory sys:path)
