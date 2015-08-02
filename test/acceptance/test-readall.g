#
# Test readAll
#

def readAfileOnce(filename)
    define fd 
        (File(.new (prepend-home filename)))
            .open ^read    
    define result (fd(.readAll))
    fd(.close)
    result

# read an empty file
assert (equal? EOF (readAfileOnce "test/fixtures/empty.txt"))

# Read a small file
define expected '123456\n\n\\end_inset\n</cell>\n<cell alignment="center" valignment="top" usebox="none">\n\\begin_inset Text\n'
define data (readAfileOnce "test/fixtures/getline.txt")
assert (equal? data expected)

# Test EOF on second read..
define fd 
    (File(.new (prepend-home "test/fixtures/getline.txt")))
        .open ^read    
fd(.readAll)
assert (equal? EOF (fd(.readAll)))
fd(.close)

