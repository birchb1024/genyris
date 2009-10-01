#
#
#

define results
   CSV(.read "testscripts/fixtures/test.csv")

assert
   equal? results
      quote
         ("#subject" "object" "predicate")
               "Joe" "type" "person"
               "Joe" "owns" "Ford"
               "Joe" "worksAt" "office"
       
