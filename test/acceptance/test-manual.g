@prefix sys "http://www.genyris.org/lang/system#"

assert
   equal?
      list 1 2 3 4 5 =
         6 7 8
      ^(1 2 3 4 5 6 7 8)

assert 
   equal?
      ^
        1 2
           3
           ~ 22
           99
      ^(1 2 (3) 22 (99))
assert
    equal? (+ 42 37) 79

def threat (i j a b)
   or
      equal? i a
      equal? j b
      equal? (- i j) (- a b)
      equal? (+ i j) (+ a b)
      
include "examples/queens.g"
run-queens 4

setq sys:path (cons (prepend-home 'examples') sys:path)
import queens
(queens.queens) 4


# This whole line is a comment 
- 4 3    # this comment goes to the end

list ^Wednesday-12 ^_age ^*global* ^+$ ^<variablename>

list -3 23.78 -100.0089  34.45e7

'"After all," said the young man, "golf is only a game."'

assertEqual
        ^(A = (B = (C = (D = nil))))
        ^(A B C D)

assertEqual
   list 1 2 4 5 = 
      6 7 8
   list 1 2 4 5 6 7 8
   
assertEqual
    ^(1 2 (3) 22 (99))
    ^
       1 2
          3
          ~ 22
          99
assertEqual
    ^(1 2 (3) (22) (99))
    ^
       1 2
          3
          22
          99

assertEqual 12 (+ 6 6)

assertEqual 12 (+ (* 2 3) (+ 2 4))

assertEqual 12 (+ 2 2 2 2 2 2)

assertEqual
    list (* 34 8) 'pears' (/ 34 5) 'kilos'
    ^
       272 'pears' 6.8 'kilos'


def factorial (n) 
   if (< n 2) 1 
      * n 
        factorial (- n 1)
assertEqual (factorial 5) 120 

assertEqual
    (function (x) (* x x)) 3
    9

assertEqual
    (lambdaq (x) (list x 'World')) (+ 'Hello')
    ^
      (+ 'Hello') 'World' 

defmacro trace(&rest body)
  print body
  body
  
trace (+ 1 2)

defmacro my-if (test success-result failure-result)
   template
      cond
         ,test ,success-result
         else ,failure-result
         
define test 3        # binding in the callers environment
assertEqual
    my-if (equal? test 3) 1 2  
    1  
assertEqual
   12 (+ 33 44) (- 4 3)
   1
assertEqual
    12 (+ .self .self) 
    24
assertEqual
    999
    12
      define foo 987
      + foo .self

12
  print .self .classes
'What am I?'.classes
^a-symbol.classes
^(3).classes

define pitt
  dict 
    .name  = "Willam Pitt"
    .title = "Prime Minister"
    .date-of-birth   = "28 May 1759"

assert 
    equal? (pitt.name) "Willam Pitt"
    
pitt
   setq .name "William Pitt The Younger"
pitt 
   define .father "William Pitt the Elder"

#assertEqual
#        pitt.vars
#        ^(title father date-of-birth name vars)

define jeb
   dict
      .firstName= "Joe" 
      .middleName= "E."
      .lastName= "Brown"
jeb 
   def .displayName()
       list .firstName .middleName .lastName
assert
    equal? 
        jeb (.displayName)
        list "Joe" "E." "Brown"

define d2
  dict
   .foo
   .bar
assert equal? nil (d1.foo)
assert equal? nil (d1.bar)

define file
  dict 
    .name  = "File Handling Functions"
    .version = "1.2"

file
   def .copy(from to) etc
   def .delete(filename) etc
   def .zip(file) etc

## Use of the file module

def archive(filename)
    file!copy filename "/tmp/foo"
    file!zip "/tmp/foo"
    file!delete filename
      
@prefix file "http://my/files/"

def file:copy(from to) etc
def file:delete(filename) etc
def file:zip(file) etc

## Use of the file module
@prefix f "http://my/files/"
def archive(filename)
    f:copy filename "/tmp/foo"
    f:zip "/tmp/foo"
    f:delete filename

@prefix sys "http://www.genyris.org/lang/system#"
setq sys:path (cons 'examples' sys:path)
import file
reload file

Bignum
   def .square() (* .self .self)
assertEqual
    4234389 (.square)
    ~ 17930050203321

assertEqual 6
   eval ^(+ 1 2 3)
   
assertEqual 6
   apply + ^(1 2 3)

def make-fn (const)
    function (a b c)
        + const a b c
define fn (make-fn 1000)
assertEqual 1111
   apply fn ^(1 10 100)
   
assertEqual ^(one (2 3) four)
    template
        one ,(list 2 3) four

assertEqual ^(one 2 3 four)
    template
        one ,@(list 2 3) four
        
assertEqual ^(comma 3) ^,3
assertEqual ^(comma-at 3) ^,@3

var t12 't12'
assertEqual
   template (,t12)
   ^('t12')

var t13 ^(13)
assertEqual
   template (foo ,@t13)
   ^(foo 13)
        
def func(a) (cons a a)
use func
   assertEqual .name 'func'
   assertEqual .source ^(lambda (a) (cons a a))

assertEqual 20 (define twenty 20)
assertEqual twenty 20

assertEqual
   'Foosball'
   defvar (intern 'new-var') 'Foosball'



def func (another-var)
   set (left (list ^another-var)) 'Tennis'
assertEqual 'Tennis' (func 123)


def square(x)
   * x x
assertEqual 64 (square 8)

df rev-quote (x)
    reverse x
assertEqual
   ^(3.4 's' 2 w +)
   rev-quote 
       + w 2 's' 3.4

defmacro @ (variable valu)
   template
      defvar ^,variable ,valu
assertEqual
   @ w 123
   123
assertEqual w 123

assertEqual ^.left 
   dynamic-symbol-value ^left

assertEqual
   45
   ^(78 = 45)
      eval (dynamic-symbol-value (intern 'right'))
      
assertEqual ^alpha
   eval ^(left ^(alpha beta gamma))

do
   define alpha 1
   assertEqual 1
      eval (left ^(alpha beta gamma))

assertEqual ^(23 = 23)
   apply
      function(x) (cons x x)
      ^(23)
