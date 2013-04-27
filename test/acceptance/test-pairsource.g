var line1 ^(1 2 3)
assertEqual 1 line1!line-number
assertEqual 
   prepend-home "test/acceptance/test-pairsource.g"
   line1!filename
var line6
   quote
      a b c
         d e f

def print-lines (x)
   cond 
      x
         print (list (x!line-number) x)
         cond
            (is-instance? (left x) Pair)
                print-lines (left x)
         print-lines (cdr x)
print-lines line6
print line6!line-number
# TODO Failing
#assertEqual 7 line6!line-number      
#assertEqual 8 
#   (right line6).line-number      
#assertEqual 9 
#   (nth 3 line6).line-number      