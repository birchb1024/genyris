##
##  Useful parsing and conversion functions.
##
##
def parse (string)
   # parse a string returning the expression
   (ParenParser(.new string))
      .read
