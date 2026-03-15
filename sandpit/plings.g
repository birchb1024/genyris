#
# PLING EXAMPLES
#

var l ^(a s d f)
var index 2
assert (equal?  ^d l!2) # index a list with a Bignum
assert (equal?  ^d l!index) # lookup a variable to index a list
print l!.vars l!.left l!left l!1


var d (dict (.a = 2))
print d!classes