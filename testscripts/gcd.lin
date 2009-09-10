#
# Rosetta Code
#
#
# finding of the greatest common divisor of two integers.
#

def gcd_test()
  assertEqual (gcd 0 0) 0
  assertEqual (gcd 0 10) 10
  assertEqual (gcd 10 0) 10
  assertEqual (gcd -10 0) 10
  assertEqual (gcd 0 -10) 10
  assertEqual (gcd 9 6) 3
  assertEqual (gcd 70 28) 14
  assertEqual (gcd 9 6) 3
  assertEqual (gcd 8 45) 1
  assertEqual (gcd 40902 24140) 34

def gcd (u v)
    u = (abs u)
    v = (abs v)
    cond
       (equal? v 0) u
       else (gcd v (% u v))
gcd_test
def gcd (u v)
    u = (abs u)
    v = (abs v)
    while (not (equal? v 0))
       var tmp (% u v)
       u = v
       v = tmp
    u
gcd_test

