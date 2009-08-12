;; Copyright 2008 Peter William Birch <birchb@genyis.org>
;;
;; This software may be used and distributed according to the terms
;; of the Genyris License, in the file "LICENSE", incorporated herein by reference.
;;
;
;  Factorial function
;

(@prefix u "http://www.genyris.org/lang/utilities#")
; next line not yet supported
;(u.format "Factorial")

(def factorial (n)
  (if (< n 2) 1
    (* n
      (factorial (- n 1))
    )
  )
)
(print factorial)
;Unit test
(and
 (equal? (factorial 0)     1)
 (equal? (factorial 1)     1)
 (equal? (factorial 2)     2)
 (equal? (factorial 3)     6)
 (equal? (factorial 4)     24)
 (equal? (factorial 5)     120)
 (equal? (factorial 6)     720)
 (equal? (factorial 7)     5040)
 (equal? (factorial 8)     40320)
 (equal? (factorial 9)     362880)
 (equal? (factorial 10)     3628800)
 (equal? (factorial 15)     1307674368000)
 (equal? (factorial 20)     2432902008176640000)
 (equal? (factorial 25)     15511210043330985984000000)
 (print "Factorial tests passed."))

