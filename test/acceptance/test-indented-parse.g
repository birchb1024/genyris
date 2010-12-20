#
# Test reading of indented syntax streams
#
var fd ((File(.new 'test/fixtures/indented.dat'))(.open ^read))
var parser (IndentedParser(.new fd))
assert
   equal? 
      ^(|http://www.foo.bar/quux#one| (two ('three') (4) (5.6)))
      parser(.read)
assert
   equal? EOF
      parser(.read)
parser(.close)
