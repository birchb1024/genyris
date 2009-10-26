def d(n)
  'DDDDD' 
    cond
        (equal? n 0)
             raise "erk"
        else
           e (- n 1)
defmacro e(n) 
    quote
       f n

def f(n) 
    if (equal? n 0)
        raise "erk"
        g (- n 1)
def g(n) 
    cond
        (equal? n 0)
             raise "erk"
        else
           d (- n 1)

d 7