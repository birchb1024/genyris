def debug(&rest args)
    stdout(.format '%s\n' args)

var path (prepend-home 'test/fixtures/rdf/example2.rdf')
#debug @LINE path
var fd
   (File(.new path))
      .open ^read
var parser (XMLParser(.new fd nil))
var result
      parser(.read)
#stdout(.format '%x' (cdr result))
debug @LINE result
@ns rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

def scan-statement(T)
    debug @LINE T

def scan(T)
    debug @LINE T
    cond
        (not (equal? ^rdf:RDF (left (left T))))
            raise 'missing rdf:RDF'
    var children (right (right (left T)))
    debug @LINE children
    for C in children
        scan-statement C
scan (right result)


