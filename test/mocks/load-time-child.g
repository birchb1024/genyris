#
# Measure how long an interpreter takes to initialise by
# spawning a new task. we get passed the start time as argv 1
# next arg is the number of times to repeat after this one.
#
@prefix u "http://www.genyris.org/lang/utilities#"
@prefix sys "http://www.genyris.org/lang/system#"

#print sys:argv
stdout(.flush)
define begin (System!ticks)
define start (nth 1 sys:argv)
u:format "Interpreter started in %a milliseconds\n" (- begin start)
define count (- (nth 2 sys:argv) 1)
#print count
stdout(.flush)
if (equal? count 0) nil
   spawn 'test/mocks/load-time-child.g' (System!ticks) count


