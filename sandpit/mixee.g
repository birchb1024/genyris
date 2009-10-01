##
##
##
include "examples/mixin.g"
@prefix m "MyMixin#"

class Mixee(m:Mixin)
  def .foo() "foo"

var d
  tag Mixee (dict)

d.m:aMethod
d.MyMixin#aMethod
