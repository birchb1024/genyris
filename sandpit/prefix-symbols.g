@ns u "http://www.genyris.org/lang/utilities#"
@ns web "http://www.genyris.org/lang/web#"
@ns sys "http://www.genyris.org/lang/system#"

@ns fubar 'https://fubar.org/quux#'

print (symbol-namespace ^web:get)
print (symbol-namespace ^sys:argv)
print (symbol-namespace ^u:format)

var fubar:alpha 42
intern 'http://foo.bar/quux'

# Look for URISymbols
for S in (symlist)
  cond
    (member? URISymbol S!classes)
      print (list 'URISymbol: ' S (symbol-namespace S) S!classes )

#Look for symbols which look like URLs or prefixed but which are not PrefixSymbols
for S in (symlist)
  var L (length ((asString S)(.split ':')))
  cond
    (> L 1)
      cond
        (null? (symbol-namespace S))
            print (list S (symbol-namespace S) (asString S) S!classes)

var fubar:alpha 42

print ^fubar:alpha

display ^fubar:alpha

u:format '%a\n' ^fubar:alpha
u:format '%s\n' ^fubar:alpha
u:format '%j\n' ^fubar:alpha
u:format '%x\n' ^fubar:alpha

var sexp ^(fubar:alpha ((fubar:quux=123)) 'one fine day')
u:format '%x\n' sexp


# Test to see what kind of symbols are generated inside the Graph/triples world
var prefixes (graph)
def list-prefixes-in (Tree)
    var P (symbol-namespace Tree)
    cond
        P
            prefixes
              .put ^true (intern(left P)) (right P)
        (is-instance? Tree Pair)
            list-prefixes-in (left Tree)
            list-prefixes-in (right Tree)

list-prefixes-in ^(fubar:quux (fubar:alpha) (gensym fubar:zulu))
print ('abbreviations found %s'(.format (prefixes(.predicates ^true))))
for T in (prefixes(.asTriples))
  print ('found abbreviation: %a = %a'(.format T!predicate T!object))



