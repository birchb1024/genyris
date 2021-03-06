define else t
define do t

define = equal

def <= (x y)
    or (< x y) (= x y)

def factorial (n)
   cond
     (<= n 1) 1
     else
        * n (factorial (- n 1))
        

do
   define tak-log nil
   def tak (x y z)
        set ^tak-log (cons (list x y z) log)
        cond 
           (< y x)
              tak 
                  tak (- x 1) y z
                  tak (- y 1) z x
                  tak (- z 1) x y
           else z
   tak 18 12 6
   the tak-log

###
### Wrap an object (views) with different perspectives. 
###
class Complex () # expects .self to be a wrapped pair ((N=N))
    def .toString()
        list "real: " (car (car .self)) "imaginary: " (cdr (car .self))

class Point ()
    def .toString()
        list "x: " (car (car .self)) "y: " (cdr (car .self))

define model ^(12=13)
define math-view ((list model)=Complex)
define plot-view ((list model)=Point)
math-view (.toString)
plot-view (.toString)


   