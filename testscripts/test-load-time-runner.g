#
# Measure how long an interpreter takes to initialise by
# spawning a new task. Pass in the time now. Do it N times.
#
spawn 'testscripts/test-load-time-child.g' (System!ticks) 10
