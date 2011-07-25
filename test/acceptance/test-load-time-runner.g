#
# Measure how long an interpreter takes to initialise by
# spawning a new task. Pass in the time now. Do it N times.
#
spawn 'test/mocks/load-time-child.g' (os!ticks) 10
