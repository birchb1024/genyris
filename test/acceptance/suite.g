## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
#
# Run Regression Tests
#
@prefix < "http://www.genyris.org/lang/utilities#"
@prefix : 'http://www.genyris.org/lib/gunit#'
@prefix sys "http://www.genyris.org/lang/system#"

include 'lib/gunit.g'

define example-files
   list
    ~ 'examples/spawn-tests.g'
    ~ 'examples/account.g'
    ~ 'examples/ackerman.g'
    ~ 'examples/class-methods.g'
    ~ 'examples/class-variables.g'
    ~ 'examples/context-sensitive-syntax.g'
    ~ 'examples/constructors.g'
    ~ 'examples/cps.g'
    ~ 'examples/executable-comments.g'
    ~ 'examples/file-module-prefixes.g'
    ~ 'examples/fun-with-brackets.g'
    ~ 'examples/lambda.g'
    ~ 'examples/length.g'
    ~ 'examples/meta-classes.g'
    ~ 'examples/new.g'
    ~ 'examples/dict-module.g'
    ~ 'examples/parametric-polymorphism.g'
    ~ 'examples/people.g'
    ~ 'examples/private_data.g'
    ~ 'examples/symbols-as-objects.g'
    ~ 'examples/triples.g'
    ~ 'examples/unixscript.g'
    ~ 'examples/url.g'
    ~ 'examples/validate.g'
    ~ 'examples/queens.g'
    ~ 'examples/allclasses.g'
    ~ 'examples/java-ffi.g'
    ~ 'examples/procedure-missing.g'

var junit-summary (sys:junit-test-runner)

def prepend-home (relative-path) (System!HOME (.+ '/' relative-path))

define spawn-files
   list
     ~ "examples/swing.g" 
     ~ "examples/swing-canvas.g" 
     ~ "examples/curves.g" 
     ~ "examples/swing-table.g"

for sp in spawn-files
   spawn (prepend-home sp)

:runTests ('%a/test/acceptance'(.format System!HOME)) example-files
<:format "Total # of JUnit tests: %s\n" junit-summary!right
<:format "Total # of JUnit errors: %s\n" junit-summary!left
assert 
   and 
      equal? 0 junit-summary!left
      equal?  0 :total-test-failed-counter
      equal? nil :failed-files

import Sound
catch ignore-errors # e.g. when running ssh over X there's no sound 
    Sound(.play (prepend-home "test/fixtures/boing.wav"))

print "DONE"
