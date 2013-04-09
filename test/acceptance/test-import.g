#
# Test nested imports.
#
@prefix sys "http://www.genyris.org/lang/system#"

def prepend-home (relative-path) (System!HOME (.+ '/' relative-path))

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
    