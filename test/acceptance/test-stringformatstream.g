#
# Test StringFormatString
#
def test-read-sfs-file()
  #
  # Read a stream from a file and check the conversion.
  #
  var fi (File(.new (prepend-home "test/fixtures/test.sfs")))
  var in (fi (.open ^read))
  var sfs (StringFormatStream(.new in))
  var expected '"Hello World "\n(+ 1 2 3 4 5)\n" --\n""A string""\n<a href=\""(+ 4 5)"\">A link</a>\n"'
  assertEqual expected
    sfs(.readAll)
  sfs(.close)
  
