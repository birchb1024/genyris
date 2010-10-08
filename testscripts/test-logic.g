#
# Logic functions
#

assert (and true true true)
assert (or true nil)
assert (not (and true nil))
catch err (and true)
assert (equal? err 'Too few arguments to and: 1')
catch err (or true)
assert (equal? err 'Too few arguments to or: 1')