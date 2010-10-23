#
#
#
import csv

define fixture
  quote
         ("#subject" "object" "predicate")
               "Joe" "type" "person"
               "Joe" "owns" "Ford, Falcon"
               "Joe" "worksAt" "office"

def testCSV (filename field-seperator quote-char)
    define fd (File(.new filename))
    define results
       csv!read (fd (.open ^read)) field-seperator quote-char
    assert
       equal? results fixture

testCSV "testscripts/fixtures/test.csv" ',' '"'
testCSV "testscripts/fixtures/test.tab" '\t' '"'
testCSV "testscripts/fixtures/test.pipe" '|' '"'
testCSV "testscripts/fixtures/test.tabpipe" '\t' '|'


      
       
