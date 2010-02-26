## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
load "testscripts/gunit.g"
@prefix t "http://www.genyris.org/lib/gunit#"
@prefix u "http://www.genyris.org/lang/utilities#"

## Test parsing & conversion functions

var overall-result
  t:test "Parsing"
    t:test "Parsing Strings"
        t:given (parse "123")       expect 123
        t:given (parse "(123 (123))")     expect (123 (123))
        t:given (equal? (parse "qwe") ^qwe)     expect true
        t:given (equal? (parse "|wqeqwe|") ^|wqeqwe|) expect true
        t:given (parse "\"22\"")        expect "22"

or overall-result (raise "Parsing Suite Failed")
