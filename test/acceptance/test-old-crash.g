#
# Regression test old exceptions. These used to throw an uncaught exception.
#
var G (graph)
catch err
    G(.put ^S ^.O 42)
assert err
assert
    (not (G(.asTriples)))

