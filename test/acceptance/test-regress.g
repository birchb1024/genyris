#
# Test infinite Regress
#

defmacro end (x)
   print x
   var av (eval x)
   cond
     (cdr av)
        template
           end ^,(cdr av)
     else
        list quote av

def last (y)
  cond
    (cdr y)
       last (cdr y)
    else
       y

define shortList ^(1 2 3 4 5 6 8 9 0)
assertEqual
   last shortList
   end shortList

def incrstack (i)
   print i
   incrstack (+ 1 i)
#
#~ 2018
#*** Error - 'Stack Overflow'
#'<EagerProc: print>'
#'<EagerProc: <incrstack>>' 181 'console'

defmacro incrloop (i)
  cond
     (equal? 100000 (eval i))
        ^^'done'
     else
        template
            incrloop ^,(+ 1 (eval i))

assertEqual 'done' (incrloop 0)




  