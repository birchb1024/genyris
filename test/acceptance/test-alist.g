#
# test the Alist class
#
var empty (tag Alist ^())
assertEqual nil (empty(.lookup ^ZZZ))
assertEqual nil (empty(.getKeys))
assert (not(empty(.hasKey ^title)))
assertEqual nil (empty(.hasKey ^ZZZ))

var assoc
  tag Alist 
    data
      about = "http://publisher/books#12312"
      title = "It Was A Dark And Stormy Night"
      author = "Fred Nurke"
      (1934 1935) = editions

assertEqual nil  (assoc(.lookup ^ZZZ))
assertEqual "It Was A Dark And Stormy Night" 
  assoc(.lookup ^title)
assertEqual ^editions 
  assoc(.lookup ^(1934 1935))
assertEqual ^(about title author (1934 1935))
  assoc(.getKeys)
assert  (assoc(.hasKey ^title))
assertEqual nil (assoc(.hasKey ^ZZZ))