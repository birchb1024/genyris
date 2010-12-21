#
# Test reading of indented syntax streams
#
var fd 
   (File(.new 'test/fixtures/indented.dat'))
      .open ^read
var parser (IndentedParser(.new fd))
assertEqual 
      ^(|http://www.foo.bar/quux#one| (two ('three') (4) (5.6)))
      parser(.read)
assertEqual 
      ^(|http://www.foo.bar/quux#two|)
      parser(.read)
assertEqual EOF
      parser(.read)
assertEqual EOF
      parser(.read)
parser(.close)

var str-parser (IndentedParser(.new "# comment\n@prefix : 'http://www.foo.bar/quux#'\n:one\n   two\n      'three'\n      4\n      5.6\n"))
assertEqual 
      ^(|http://www.foo.bar/quux#one| (two ('three') (4) (5.6)))
      str-parser(.read)
assertEqual EOF
      str-parser(.read)

