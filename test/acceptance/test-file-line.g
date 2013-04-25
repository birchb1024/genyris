define line1 @LINE
define line2 @LINE 
define line3 @LINE #
define line6 nil
do
   setq line6 @LINE
assertEqual line1 1
assertEqual line2 2
assertEqual line3 3
assert
    equal? line6 6
catch error
   raise @LINE 
assertEqual error 13
print
do
   assertEqual @LINE 17
print
assertEqual @LINE 19
assertEqual @LINE 20 #
do (assertEqual @LINE 21) (+ 2 3) 
do 
  do
      assertEqual @LINE 24
assertEqual @FILE 
   prepend-home 'test/acceptance/test-file-line.g'

catch error
   raise ("throw here %a %a"(.format @FILE @LINE)) 
assertEqual error 
   'throw here %a/test/acceptance/test-file-line.g 29'
      .format System!HOME
   