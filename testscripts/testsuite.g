## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
#
# Run Regression Tests
#
@prefix : 'http://www.genyris.org/lib/gunit#'
include 'lib/gunit.g'

#include 'testscripts/examples-as-tests.g'

:runTests 'testscripts'
assertEqual 0 total-test-failed-counter
assertEqual nil failed-files
   