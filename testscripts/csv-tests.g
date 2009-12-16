#
#
#
define fd (File(.new "testscripts/fixtures/test.csv"))
define results
   CSV(.read (fd (.open ^read)))

assert
   equal? results
      quote
         ("#subject" "object" "predicate")
               "Joe" "type" "person"
               "Joe" "owns" "Ford"
               "Joe" "worksAt" "office"
       
