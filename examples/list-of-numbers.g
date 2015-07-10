#
# Simple example of List of numbers
#
include 'lib/classify.g'


class ListOfNumbers()
    #
    # A simple list of BigNums like this: ^(18 13 12 8.1 7.9 7.4 7.3 6)
    #
    def valid?(obj)
       cond
          (null? obj) nil
          (null? (right obj))
             is-instance? (left obj) Bignum
          else
               and (is-instance? (left obj) Bignum)
                    valid? (right obj)
    
    def .valid?(obj) (valid? obj)
    
    def .sum()
        # Arithmetic sum of the numbers
        apply (the +) .self
        
    def .max()
        # Largest number
        define currentmax (left .self)
        for i in .self
            cond
                (> i currentmax)
                    setq currentmax i
        the currentmax

assert (not(ListOfNumbers!valid? nil))
assert (ListOfNumbers!valid? ^(1))
assert (ListOfNumbers!valid? ^(1 2))
assertEqual 1 ((tag ListOfNumbers ^(1))(.max))
assertEqual 2 ((tag ListOfNumbers ^(1 2))(.max))


define x ^(18 13 12 8.1 7.9 7.4 7.3 6.1 5.6 5.6 4.2 3.2 2.6 1.7 1.3)
classify ListOfNumbers x
    
assertEqual 104.0 (x(.sum))
assertEqual 18 (x(.max))

