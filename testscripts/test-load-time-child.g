#
# Measure how long an interpreter takes to initialise by
# spawning a new task. we get passed the start time as argv 1
# next arg is the number of times to repeat after this one.
#
@prefix u "http://www.genyris.org/lang/utilities#"
@prefix sys "http://www.genyris.org/lang/system#"

define now ((System.ticks))
define start (parse(nth 1 sys:argv))
u:format "Interpreter started in %a milliseconds\n" (- now start)
define count (- (parse(nth 2 sys:argv)) 1)
if (equal? count 0) nil
   spawn 'testscripts/test-load-time-child.g' ((System.ticks)) count


