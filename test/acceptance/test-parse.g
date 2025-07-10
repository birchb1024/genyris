## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@ns : "http://www.genyris.org/lib/gunit#"
@ns u "http://www.genyris.org/lang/utilities#"

## Test parsing & conversion functions

"Parsing"
    :test "Parsing Strings"
        :assertEqual (parse "123") 123
        :assertEqual (parse "(123 (123))") ^(123 (123))
        :assertEqual (equal? (parse "qwe") ^qwe)     true
        :assertEqual (equal? (parse "|wqeqwe|") ^|wqeqwe|) true
        :assertEqual (parse "\"22\"")        "22"

