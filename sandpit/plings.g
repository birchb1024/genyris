#
# PLING EXAMPLES
#
@ns u "http://www.genyris.org/lang/utilities#"

var l ^(a s d f)
var index 2
assert (equal?  ^d l!2) # index a list with a Bignum
assert (equal?  ^d l!index) # lookup a variable to index a list
print l!.vars l!.left l!left l!1


var d (dict (.a = 2))
print d!classes

var g (graph)
g
  .put ^sandnes ^locode 'ABCD'
  .put ^sandnes ^coords  ^coord-s

  .put ^coord-s ^longitude 3.0
  .put ^coord-s ^latitude 3.0
u:format "%a %s\n" @LINE g
u:format "%a %s\n" @LINE (g(.subjects))
u:format "%a %s\n" @LINE ^(g .subjects)
u:format "%a %s\n" @LINE ^( g;subjects )
u:format "%a %s\n" @LINE ^( g!subjects )

u:format "%a %s\n" @LINE (quote g!^sandes)
u:format "%a %s\n" @LINE (quote g!sandes)
u:format "%a %s\n" @LINE g!sandes
u:format "%a %s\n" @LINE g!sandnes!locode
u:format "%a %s\n" @LINE g!sandnes!cord
u:format "%a %s\n" @LINE g!sandnes!coord!latitude
