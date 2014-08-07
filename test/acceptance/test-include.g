#
# Test nested imports.
#
@prefix sys "http://www.genyris.org/lang/system#"

load "org/genyris/load/boot/pair.g" true

catch err
   include "does-not-exist.g"
print
  equal? err 'include: could not locate "does-not-exist.g"'
assert (equal? err 'include: could not locate "does-not-exist.g"')

catch err
   include 234234
assert (equal? err 'Type mismatch in function call for (filename = String) because validator error: object 234234 is not tagged with String')

catch err
   load 'does-not-exist.g'
assert (equal? err 'loadScriptFromInputStream: could not open: does-not-exist.g')

catch err
   load 234234
assert (equal? err 'non-string file path argument passed to load: 234234')

# test include with sys:path
setq sys:path (cons ((System.HOME) (.+ '/test/fixtures')) sys:path)
include 'empty.g'
assert empty-loaded