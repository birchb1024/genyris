##
##
##

# http://en.wikipedia.org/wiki/Diamond_problem :
# For example, a class Button inherits from both classes Rectangle (for appearance)
# and Mouse (for mouse events), and classes Rectangle and Mouse both inherit from
# the Object class. Now if the equals method is called for a Button object and
# there is no such method in the Button class but there is an over-ridden equals
# method in both Rectangle and Mouse, which method should be called?

class Obj()
  def .equals () "Obj.foo"
class Rectangle(Obj)
  def .equals () "B.foo"
class Mouse(Obj)
  def .equals () "C.foo"
class Button(Rectangle Mouse)

var it (tag Button (dict))

it(.equals)
