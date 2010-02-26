#
#
#
load "testscripts/gunit.g"
@prefix t "http://www.genyris.org/lib/gunit#"
@prefix myprefix "http://foo/#"

var myprefix:foo 0

@prefix p1 "http://p1/#"
#assert
#    equal? (intern "http://foo/#foo") ^p1:foo
var overall-result
  t:test "prefixes"
    t:test "basic prefixes"
        t:given ^(intern "http://p1/#foo") expect ^p1:foo
        t:given ^(intern "http://null/prefix#foo") expect ^|http://null/prefix#foo|

    t:test "nested prefixes"
        load "testscripts/prefix-test-nested.g"
        t:given myprefix:foo expect 0
or overall-result (raise "Prefixes Failed")