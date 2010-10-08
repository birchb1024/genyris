#
# Random tests missing - added to improve coverage
#

catch error
  setq nil 42
assert (equal? error 'Attempt to re-define nil!')
#
# use of procs as objects
#
def func(a) (cons a a)
use func
   assert (equal? .name 'func')
   assert (equal? .source ^(lambda (a) (cons a a)))
   assert (equal? .vars ^(source name .self .vars .classes))

# set to an unbound var
catch err
   setq western-unbound 234
assert (equal? err 'unbound: western-unbound')

# user error
catch err
   raise "fooey!"
assert (equal? err 'fooey!')

assert (symlist)

assert
   equal? '<LazyProcedure: http://www.genyris.org/lang/system#backtrace>'
      left (|http://www.genyris.org/lang/system#backtrace|)