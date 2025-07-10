@ns u "http://www.genyris.org/lang/utilities#"

@ns fubar 'https://fubar.org/quux#'

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


