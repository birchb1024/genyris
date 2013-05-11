
var f (File(.new (prepend-home 'test/fixtures/agile.txt')))
var fd (f(.open ^read))
for line in fd
  print line
fd(.close)

