## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
include "lib/gunit.g"
@prefix : "http://www.genyris.org/lib/gunit#"
@prefix u "http://www.genyris.org/lang/utilities#"

## Test built-in interning
@prefix p "http://server/#"

:test-suite "Symbol Tests"
      :test "Escaped Intern tests"
        :assertEqual (intern "foo") ^|foo|
        :assert (not (equal? (gensym "foo") ^|foo|))
        :assertEqual (intern "http://server/#w") ^|http://server/#w|
      :test "Intern tests"
        :assertEqual ((intern "foo").classes) (list SimpleSymbol)
        :assertEqual ((intern "http://server/#w").classes) (list URISymbol)
        :assertEqual (intern "A")      ^A
        :assertEqual (intern "http://server/#w") ^p:w
        :assertEqual (left (member? SimpleSymbol ((intern 23).classes))) SimpleSymbol
      :test "gensym tests"
        :assertEqual (eq? (gensym 23) (gensym 23)) nil
        :assertEqual (equal? (gensym 23) (gensym 23)) nil
