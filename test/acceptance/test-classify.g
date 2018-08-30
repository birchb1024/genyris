
include "lib/classify.g"

#
# Make sure derived class .valid? is not picked up by classifier
#
class A(String)
  def .valid?(o)
    is-instance? o String

class B(A)

var foo 'a string'

classify A foo

assert (member? A foo!classes)
assert (not (member? B foo!classes))
