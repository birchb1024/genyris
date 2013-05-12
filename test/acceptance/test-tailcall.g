#
# infinite recursion is only possible with tailcall elimination.
# So run some infinite functions beyond where a stack overflow would normally occur.
#

# first cause a stack overflow and count the depth
var stackmax 0
def overflow(i)
   setq stackmax i
   overflow (+ i 1)

catch error
   overflow 0

def infinite(i)
   # print i
   cond 
      (equal? i 0) 0
      else
         tailcall infinite (- i 1)

# call with tailcalls eliminated and run twice as long.
infinite (* 2 stackmax)

def infinite-duo-A(i)
   # print i
   cond 
      (equal? i 0) 0
      else
         tailcall infinite-duo-B (- i 1)

def infinite-duo-B(i)
   # print i
   cond 
      (equal? i 0) 0
      else
         tailcall infinite-duo-A (- i 1)

infinite-duo-A (* 2 stackmax)
