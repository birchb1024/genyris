#
# Rosetta Code - The Ackerman Function
#
def A (m n)
   cond
      (equal? m 0)
          + n 1
      (equal? n 0) 
          A (- m 1) 1
      else
          A (- m 1)
             A m (- n 1)

assertEqual (A 2 2) 7             
assertEqual (A 3 4) 125
