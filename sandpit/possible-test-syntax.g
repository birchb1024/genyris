## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
tests "some reason to test"

  setup
    define var 56
  
  test "name"
    execute
       do something
       do something
    post-conditions
       null? var

  test "name"
    execute
       do something
       do something
    post-conditions
       null? var