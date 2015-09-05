#
#
#
import csv

define fixture
  data
    "#subject" "object" "predicate"
    "Joe" "type" "person"
    "Joe" "owns" "Ford, Falcon"
    "Joe" "worksAt" "office"

def testCSV (filename field-seperator quote-char)
    define fd (File(.new (prepend-home filename)))
    define results
       csv!read (fd (.open ^read)) field-seperator quote-char
    assert
       equal? results fixture

testCSV "test/fixtures/test.csv" ',' '"'
testCSV "test/fixtures/test.tab" '\t' '"'
testCSV "test/fixtures/test.pipe" '|' '"'
testCSV "test/fixtures/test.tabpipe" '\t' '|'
