#
# Test reading of indented syntax streams
#
var fd 
   (File(.new (prepend-home 'test/fixtures/types.xml')))
      .open ^read
var parser (XMLParser(.new fd nil))
var result 
      parser(.read)
stdout(.format '%j' (cdr result))


#var fd 
#   (File(.new (prepend-home 'test/fixtures/rootservices.xml')))
#      .open ^read
#var parser (XMLParser(.new fd))
#var result 
#      parser(.read)
#print result

