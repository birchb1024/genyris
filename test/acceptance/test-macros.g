
#
# nested macro test case
#
var L1 ^(1 2 3)
var L2 ^(X Y Z)
var L3 ^(A B C)
var result nil
for v1 in L1
   for v2 in L2
      for v3 in L3
          setq result (cons (list v1 v2 v3) result)
var expected
   ^((3 Z C) (3 Z B) (3 Z A) (3 Y C) (3 Y B) (3 Y A) (3 X C) (3 X B) (3 X A) (2 Z C) (2 Z B) (2 Z A) (2 Y C) (2 Y B) (2 Y A) (2 X C) (2 X B) (2 X A) (1 Z C) (1 Z B) (1 Z A) (1 Y C) (1 Y B) (1 Y A) (1 X C) (1 X B) (1 X A))
assert (equal? result expected)      

#
# Genysm fix for Variable name clashes.
# From http://www.apl.jhu.edu/~hall/Lisp-Notes/Macros.html
# 
defmacro Square-Sum-Broken (X Y)
   template
      do
         define First ,X
         define Second ,Y
         define Sum (+ First Second)
         * Sum Sum

defmacro Square-Sum (X Y)
   define First (gensym 'First')
   define Second (gensym 'Second')
   define Sum (gensym 'Sum')
   template
      do
         define ,First ,X
         define ,Second ,Y
         define ,Sum (+ ,First ,Second)
         print ,Sum
         * ,Sum ,Sum

define First 9
Square-Sum-Broken 1 First
define First 9
assert
  equal?
     Square-Sum 1 First
     100

