#
# Class Variables example from "Design Patterns in Ruby", Russ Olsen.
#
class ClassVariableTester (Object)
   define class_count 0

   def .get-class-count()
      the class_count

   def .init()
      define .instance_count 0

   def .increment()
      setq class_count (+ class_count 1)
      setq .instance_count (+ .instance_count 1)
      list class_count .instance_count

var c1
  ClassVariableTester(.new)
var c2
  ClassVariableTester(.new)
assert
    equal? (c1(.increment)) ^(1 1)
assert
    equal? (c2(.increment)) ^(2 1)



