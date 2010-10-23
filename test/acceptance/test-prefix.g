#
#
#
@prefix : "http://www.genyris.org/lib/gunit#"
@prefix myprefix "http://foo/#"

var myprefix:foo 0

@prefix p1 "http://p1/#"
#assert
#    equal? (intern "http://foo/#foo") ^p1:foo
do
    :test "basic prefixes"
        :assertEqual (intern "http://p1/#foo") ^p1:foo
        :assertEqual (intern "http://null/prefix#foo") ^|http://null/prefix#foo|

    :test "nested prefixes"
        include "test/mocks/prefix-test-nested.g"
        :assertEqual myprefix:foo 0
