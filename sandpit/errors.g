
def r(N)
    cond
        (equal? N 0)
            print 34
            raise ^Done
        else
            r (- N 1)
r 3

print undef
