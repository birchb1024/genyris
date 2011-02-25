#
# Example of hooking undefined function calls.
#
@prefix sys "http://www.genyris.org/lang/system#"

def sys:procedure-missing(&rest args)
  cond
     (is-instance? (left args) DynamicSymbolRef)
          .method_missing args
     else
          raise ("procedure missing: %s"(.format args))
            
class MyClass()
   def .method_missing(args)
        list 'meta-method' args

var myObject
    tag MyClass (dict)

assertEqual
   myObject(.setName 'fred')
   ^('meta-method' (.setName 'fred'))          


