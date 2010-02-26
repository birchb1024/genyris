## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
load "testscripts/gunit.g"
@prefix t "http://www.genyris.org/lib/gunit#"
@prefix u "http://www.genyris.org/lang/utilities#"

## Test built-in maths functions


assertEqual (abs 0) 0
assertEqual (abs -1) 1
assertEqual (abs 1) 1

var overall-result
  t:test "Maths Suite"
    t:test "Integer Additions"
        t:given (+ 1 3)       expect 4
        t:given (+ 0 0 0)     expect 0
        t:given (+ 1 2 3)     expect 6
        t:given (+ 1 2 3 4 5) expect 15
        t:given (+ 42)        expect 42

    t:test "Subtraction"
        t:given (- 0 1)       expect -1
        t:given (- 3 2 1)     expect 0
        t:given (- -1 -2)     expect 1
        t:given (- 77)        expect -77

    t:test "Multiplication"
        t:given (* 0 1)       expect 0
        t:given (* 3 2 1)     expect 6
        t:given (* 1 -2)      expect -2

    t:test "Remainder"
        t:given (% 5 4)       expect 1
        t:given (% 2131 23)   expect 15

    t:test "Power"
        t:given (power 5 4)       expect 625
        t:given (power 2131 23)   expect 36092697650034702295853815450637695553542082573689404886956794546447063421691
        t:given (power 2 8)       expect 256

    t:test "Greater then"
        t:given (> 5 4)       expect true
        t:given (> 4 4)       expect nil
        t:given (> 4 4)       expect nil

    t:test "Less than"
        t:given (< 5 4)       expect nil
        t:given (< 4 4)       expect nil
        t:given (< 3 4)       expect true

    t:test "Equal"
        t:given (equal? 5 4)       expect nil
        t:given (equal? 4 4)       expect true
        t:given (equal? 3.4 3.4000001)       expect nil


or overall-result (raise "Maths Suite Failed")
