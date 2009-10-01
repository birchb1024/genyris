#
#
#
@prefix sys "http://www.genyris.org/lang/system#"

define private-var1 123
define .public-var1 145

def private1() ^private1

def .public1()
    list ^public1 (private1) private-var1 .public-var1


# nested import
sys:import "testscripts/test-module2.g"