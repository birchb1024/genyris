###
### The Secret Life of Symbols 
###
# Symbols can execute code:
^y
   assertEqual 
        list .self (+ 2 3)
        ^(y 5)
#  
# Symbols can be in classes:
#
class Keyword()
   var .syntaxHightlightColor "Red"
tag Keyword ^if
assertEqual (^if .syntaxHightlightColor) "Red"




