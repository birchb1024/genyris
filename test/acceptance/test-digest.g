#
# Test Stream Digest Function
#

#
assertEqual 
    (Reader!new "")
        .digest "MD5"
    "d41d8cd98f00b204e9800998ecf8427e"

assertEqual 
    (Reader!new "\n")
        .digest "MD5"
    "68b329da9893e34099c7d8ad5cb9c940"

assertEqual 
    (Reader!new "secret")
        .digest "MD5"
    "5ebe2294ecd0e0f08eab7690d2a6ee69"


# Test a stream from a small text file
var fi (File!new 'test/fixtures/secret.txt') # Contains just 'secret'
define fd (fi(.open ^read))
assertEqual "5ebe2294ecd0e0f08eab7690d2a6ee69"
    fd(.digest "MD5")
    
    
# Test a stream from a larger text file
var fi (File!new 'test/fixtures/factorial.g')
define fd (fi(.open ^read))
assertEqual "8ad8daca30fd67906f68d9390143cdfa"
    fd(.digest "MD5")
    

# TODO Binary streams?    
# Test a stream from a larger binary file
#var fi (File!new 'test/fixtures/boing.wav') # Binary
#define fd (fi(.open ^read))
#assertEqual "2896cdf4887685122aa2afad097232a6"
#    fd(.digest "MD5")
    