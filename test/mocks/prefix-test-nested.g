@prefix myprefix "http://nested-prefix#"
var myprefix:foo 1
assert
    equal? (intern "http://nested-prefix#foo") ^myprefix:foo
assert
   equal? myprefix:foo 1