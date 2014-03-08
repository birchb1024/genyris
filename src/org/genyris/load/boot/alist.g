## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

#
#  An association list
#
class Alist()

   def renderRow(apair)
      template
          tr()
              td() $(left apair)
              td() $(right apair)

   def renderRows(alist)
       cond
          (null? alist) nil
          else
             cons
                renderRow (alist.left)
                renderRows (alist.right)

   def lookup(key list)
      cond
          (null? list) nil
          (equal? key ((list.left).left))
             (list.left).right
          else
              lookup key (list.right)

   def mapKeys(alist)
      cond
        alist
          cons
             left(left alist)
             mapKeys (right alist)

   def .lookup(key)
      lookup key .self

   def .getKeys()
      mapKeys(.self)

   def .hasKey(key)
      member? key (.getKeys)

   def .toHTML()
      template
          table ((border = 1) (cellpadding = 4))
             $@(renderRows .self)

