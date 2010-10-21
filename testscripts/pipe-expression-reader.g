#
# Pipes Example - read an expression from a pipe 
#
@prefix task 'http://www.genyris.org/lang/task#'
@prefix sys 'http://www.genyris.org/lang/system#'
@prefix u "http://www.genyris.org/lang/utilities#"

print
  "Reading from %s\n"
     .format (nth 1 sys:argv)
define inpipe (Pipe(.open (nth 1 sys:argv)))

define in (inpipe(.input))
define parser (ParenParser(.new in))
print in parser
var exp (parser (.read))
while (not (equal? exp EOF))
    u:format "reader task received expression: %s\n" exp
    setq exp (parser (.read))


