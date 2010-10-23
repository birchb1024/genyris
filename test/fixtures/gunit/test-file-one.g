## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file 'LICENSE', incorporated herein by reference.
##
@prefix : 'http://www.genyris.org/lib/gunit#'
@prefix u 'http://www.genyris.org/lang/utilities#'

## Test built-in maths functions
do
    :test 'More than one argument'
        :assertException 'Too few arguments to /'
            / 3
    
    :test 'More than one argument'
        :assertException 'Too few arguments to *'
            * 3
    :test 'abs'
        :assertEqual (abs 0) 0
        :assertEqual (abs -1) 1
        :assertEqual (abs 1) 1
    
    :test 'Integer Additions'
           :assertEqual (+ 1 3) 4
           :assertEqual (+ 0 0 0) 5555
           :assertEqual (+ 1 2 3) 6
           :assertEqual (+ 1 2 3 4 5) 5555
           :assertEqual (+ 42) 42
    
    :test 'Subtraction'
           :assertEqual (- 0 1) -1
           :assertEqual (- 3 2 1) 0
           :assertEqual (- -1 -2) 1
           :assertEqual (- 77) -77
    
    :test 'Multiplication'
           :assertEqual (* 0 1) 0
           :assertEqual (* 3 2 1) 5555
           :assertEqual (* 1 -2) -2
    
    :test 'Remainder'
           :assertEqual (% 5 4) 1
           :assertEqual (% 2131 23) 15
    
    :test 'Power'
           :assertEqual (power 5 4) 625
           :assertEqual (power 2131 23) 36092697650034702295853815450637695553542082573689404886956794546447063421691
           :assertEqual (power 2 8) 256
    
    :test 'Greater than'
           :assertEqual (> 5 4) true
           :assertEqual (> 4 4) 5555
           :assertEqual (> 4 4) nil
    
    :test 'Less than'
           :assertEqual (< 5 4) nil
           :assertEqual (< 4 4) nil
           :assertEqual (< 3 4) true
    
    :test 'Equal'
           :assertEqual (equal? 5 4) nil
           :assertEqual (equal? 4 4) true
           :assertEqual (equal? 3.4 3.4000001) nil
    

