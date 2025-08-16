@ns u "http://www.genyris.org/lang/utilities#"
@ns web "http://www.genyris.org/lang/web#"
@ns sys "http://www.genyris.org/lang/system#"

@ns fubar 'https://fubar.org/quux#'

print (symbol-namespace ^web:get)
print (symbol-namespace ^sys:argv)
print (symbol-namespace ^u:format)

var fubar:alpha 42
intern 'http://foo.bar/quux'

for S in (symlist)
  cond
    (member? URISymbol S!classes)
      print (list 'URISymbol: ' S (symbol-namespace S) S!classes )

for S in (symlist)
  var L (length ((asString S)(.split ':')))
  cond
    (> L 1)
      cond
        (null? (symbol-namespace S))
            print (list S (symbol-namespace S) (asString S) S!classes)
#        list
#          ~ S
#          ~ L
#          ~ (> L 1)
#          ~ (asString S)
#          ~ (cond ((> L 1)(the S)))

os!exit
var fubar:alpha 42

print ^fubar:alpha

display ^fubar:alpha

u:format '%a\n' ^fubar:alpha
u:format '%s\n' ^fubar:alpha
u:format '%j\n' ^fubar:alpha
u:format '%x\n' ^fubar:alpha

var sexp ^(fubar:alpha ((fubar:quux=123)) 'one fine day')
u:format '%x\n' sexp

var prefixes-list (graph)
def list-prefixes-in (Tree)
    var P (symbol-namespace Tree)
    #print @LINE P
    cond
        (P)
            prefixes-list(.put (intern(left P)) (intern(right P)) true)
        (is-instance? Tree Pair)
            list-prefixes-in (left Tree)
            list-prefixes-in (right Tree)
symbol-namespace ^fubar:c

list-prefixes-in ^(fubar:quux (fubar:alpha) fubar:beta)

print (prefixes-list(.asTriples))

