#
# Rosetta Code
#
# The Y combinator is itself a stateless function that, when applied to another
# stateless function, returns a recursive version of the function.
# The Y combinator is the simplest of the class of such functions, called fixed-point combinators.
#
# The task is to define the stateless Y combinator and use it to compute factorials
# and Fibonacci numbers from other stateless functions or lambda expressions.
#
def fac (f)
    lambda (n)
      if (equal? n 0) 1
        * n (f (- n 1))
def fib (f)
  lambda (n)
    cond
      (equal? n 0) 0
      (equal? n 1) 1
      else (+ (f (- n 1)) (f (- n 2)))

def Y (f)
  (lambda (x) (x x))
      lambda (y)
          f
              lambda (&rest args) (apply (y y) args)

assertEqual ((Y fac) 5) 120
assertEqual ((Y fib) 8) 21
