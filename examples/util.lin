## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##

StandardClass
  # method to dump out the class hierarchy of the class
  def .getClassTree()
    cons .classname
        map-left .subclasses getClassTree
