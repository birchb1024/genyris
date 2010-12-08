#
#
#
define fd 
   (File(.new "test/fixtures/getline.txt"))
      .open ^read
assert (fd(.hasData))
assert (fd(.hasData))
assertEqual '123456' (fd(.getline))
assert (fd(.hasData))
assertEqual '' (fd(.getline))
assert (fd(.hasData))
assertEqual '\\end_inset' (fd(.getline))
assert (fd(.hasData))
assertEqual '</cell>' (fd(.getline))
assert (fd(.hasData))
assertEqual '<cell alignment="center" valignment="top" usebox="none">' (fd(.getline))
assert (fd(.hasData))
assertEqual '\\begin_inset Text' (fd(.getline))
assert (not (fd(.hasData)))

