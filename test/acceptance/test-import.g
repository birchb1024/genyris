#
# Test nested imports.
#
@prefix sys "http://www.genyris.org/lang/system#"

assertEqual (prepend-home 'foo') ('%a/foo' (.format System!HOME))
cond
   (((os!getProperties).|os.name|)(.match 'Windows.*'))
         assert (isAbsolutePath? '/foo')
         assert (isAbsolutePath? 'C:/foo/bar')
         assert (isAbsolutePath? 'C:/Program Files/bar')
         assert (isAbsolutePath? 'z:/Program Files/bar')
cond
   (((os!getProperties).|os.name|)(.match 'Linux.*'))
         assert (isAbsolutePath? '/foo')
         assert (isAbsolutePath? '/foo/bar')
         assert (isAbsolutePath? '/Program Files/bar')

catch err
   sys:import "doesnot-exist.g"
assert (equal? err 'loadScriptFromFile: doesnot-exist.g (No such file or directory)')

catch err
   sys:import 232323
assert (equal? err 'non-String argument passed to sys:import: 232323')

(var modulex (dict))
    sys:import (prepend-home "test/fixtures/module1.g")
  
assertEqual 
    modulex(.public1)
    list ^public1 ^private1 123 145
    
assertEqual 
    modulex(.public2)
    list ^public2 ^private2 222 444

setq sys:path (cons (prepend-home "test/fixtures") sys:path)
import module1
assertEqual 
    module1(.public1)
    list ^public1 ^private1 123 145
assertEqual 
    module1(.public2)
    list ^public2 ^private2 222 444
    