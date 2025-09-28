@ns sys "http://www.genyris.org/lang/system#"

def prepend-home (relative-path)
    System!HOME (.+ '/' relative-path)

assertEqual sys:script-directory (prepend-home 'test/fixtures')
