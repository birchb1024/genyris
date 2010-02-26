#
# Test nested imports.
#
@prefix sys "http://www.genyris.org/lang/system#"

(var modulex (dict))
    sys:import "testscripts/test-module1.g"
  
assertEqual 
    modulex(.public1)
    list ^public1 ^private1 123 145
    
assertEqual 
    modulex(.public2)
    list ^public2 ^private2 222 444

sys:path = (cons "testscripts" sys:path)
import test-module1
assertEqual 
    test-module1(.public1)
    list ^public1 ^private1 123 145
assertEqual 
    test-module1(.public2)
    list ^public2 ^private2 222 444
    