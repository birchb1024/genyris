#
# Test hooking undefined function calls.
#
@prefix sys "http://www.genyris.org/lang/system#"

#
# Test to make sure that if sys:procedure-missing is broken it gives
# an error not a stack overflow.
#
def sys:procedure-missing(&rest args)
  an-undefined-function
  
catch error
    call-a-function-which-does-not-exist
assertEqual error 'Unbound symbol within http://www.genyris.org/lang/system#procedure-missing unbound variable: an-undefined-function'

