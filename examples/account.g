## Copyright 2009 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

# Example from SICP Section 3.2.3
def make-withdraw (balance)
   lambda (amount)
      setq balance (- balance amount)

define W1 (make-withdraw 100)

assert
  equal? (W1 25) 75
assert
  equal? (W1 25) 50
    
