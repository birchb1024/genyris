#
# Test nested imports.
#
@prefix sys "http://www.genyris.org/lang/system#"

include 'test/fixtures/empty.g' true
load "org/genyris/load/boot/pair.g" true

catch err
   include 'does-not-exist.g'
assert (equal? err 'loadScriptFromFile: does-not-exist.g (No such file or directory)')    

catch err
   include 234234
assert (equal? err 'non-string argument passed to include: 234234')    

catch err
   load 'does-not-exist.g'
assert (equal? err 'loadScriptFromInputStream: could not open: does-not-exist.g')    

catch err
   load 234234
assert (equal? err 'non-string argument passed to load: 234234')    