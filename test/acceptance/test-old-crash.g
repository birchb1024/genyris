#
# Regression test old exceptions. These used to throw an uncaught exception.
#

# exception crash with dynamic symbol in triple

var G (graph)
catch err
    G(.put ^S ^.O 42)
assert err
assert
    (not (G(.asTriples)))

# exception crash with bad args to date:format
@ns date "http://www.genyris.org/lang/date#"
def test-fd(&rest args)
    eval
        template
          do
            catch err
                date:format-date $@args
            #print err
            assert err

test-fd 0 37
test-fd 42
test-fd 0 '2'
test-fd 0 666 666
test-fd 0 'foo' 666
test-fd 0 'fubar' ''
