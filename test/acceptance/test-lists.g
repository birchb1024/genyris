@prefix : 'http://www.genyris.org/lib/gunit#'
@prefix < "http://www.genyris.org/lang/utilities#"

:test 'append'
        :assertEqual (append ^(1 2) ^(3 4)) ^(1 2 3 4)
        :assertEqual (append nil nil) nil
        :assertEqual (append nil ^(A)) ^(A)
        :assertEqual (append ^(B) nil) ^(B)
        :assertEqual (append ^(C) ^(D)) ^(C D)
        :assertException 'attempt to take car of non-pair: E'
            append ^E ^(3 4)
        :assertEqual (append ^(3 4) ^F) ^(3 4 = F)

assert (beginsWith? ^(1 2) ^(1 2 3))
assert (beginsWith? ^(1 2) ^(1 2))
assert (beginsWith? ^(1) ^(1))
assert (not (beginsWith? ^(1) ^(2)))
assert (not (beginsWith? ^(1 2 3) ^(1 2)))

