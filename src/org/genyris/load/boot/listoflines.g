## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  An ordered list of strings
#
ListOfLines

   def .toHTML()
      template
          pre()
             $@(renderLines .self)

   def renderLine(aLine)
      template
          $aLine (br())

   def renderLines(lines)
       cond
          (null? lines) nil
          else
             cons
                renderLine (lines.left)
                renderLines (lines.right)

# var x
#   tag ListOfLines ^("1" "2" "3")
