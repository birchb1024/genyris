
catch err1
  assert nil
cond
  (not (equal? 'macro assert failed on: nil value: nil' err1))
    raise (list 'FAIL wrong error caught' err1)

#
# Use regex match becuase the file path is not always the same, ide, vs cfrom shell.
# Also regex match the line numbers so lines can be added without breaking the tests
#
var expected2 '.*test-assert.g:[0-9]+ macro assert failed on: \\(not 1\\) value: nil$'
catch err2
  assert (not 1)
cond
  (not (err2(.match expected2)))
    raise (list 'FAIL wrong error caught' err2 expected2)

catch err3
  assert true
cond
  err3
    raise (list 'FAIL wrong error caught' err3)

catch err4
  assertEqual 1 1
cond
  err4
    raise (list 'FAIL wrong error caught' err4)

catch err5
  assertEqual 2 3
cond
  (not (equal? 'macro assertEqual failed on: 2 3 values: 2 3' err5))
    raise (list 'FAIL wrong error caught' err5)

var expected6 '.*test-assert.g:[0-9]+ macro assertEqual failed on: 1 \\(\\+ 0 2\\) values: 1 2$'
catch err6
  assertEqual 1 (+ 0 2)
cond
  (not (err6(.match expected6)))
    raise (list 'FAIL wrong error caught' err6 expected6)

var expected7 '.*test-assert.g:[0-9]+ macro assertEqual failed on: \\(\\+ 0 1\\) 2 values: 1 2$'
catch err7
  assertEqual (+ 0 1) 2
cond
  (not (err7(.match expected7)))
    raise (list 'FAIL wrong error caught' err7 expected7)
