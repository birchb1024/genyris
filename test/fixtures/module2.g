#
#
#

define private-var2 222
define .public-var2 444

def private2() ^private2

def .public2()
    list ^public2 (private2) private-var2 .public-var2
