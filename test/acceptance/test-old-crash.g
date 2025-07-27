#
# Regression test old exceptions. These used to throw an uncaught exception. 
#
var G (graph)
G(.put ^S ^.O 42)
print
    G(.asTriples)

