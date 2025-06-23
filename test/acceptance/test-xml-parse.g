#
# Test reading of indented syntax streams
#
var fd 
   (File(.new (prepend-home 'test/fixtures/employees.xml')))
      .open ^read
var parser (XMLParser(.new fd))
var result 
      parser(.read)
print result


var fd 
   (File(.new (prepend-home 'test/fixtures/rootservices.xml')))
      .open ^read
var parser (XMLParser(.new fd))
var result 
      parser(.read)
print result

