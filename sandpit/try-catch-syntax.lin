@prefix ex "http://rdf.genyris.org/Exception#"
class ex.Error()
class ex:FooError(ex:Error)
class ex:BarError(ex:Error)
try
  do
    System
      apply _exec ^("cmd.exe" "/c" "dir")
  catch ex:FooError
    stuff
  catch ex:BarError
    stuff
  finally
    clean up
  