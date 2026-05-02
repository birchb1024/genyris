#
# shift - traverse a tree within a Graph
#
# Description
#
# When given a Graph and subject, the shift functionselects all the triples with that subject.
# It then creates a new Graph where the predicates become the subjects, and the objects are interned as
# predicates.
#
# e.g.
#
# var G (graph ^(q w e) ^(a s d) ^(q a z))
# 
# (triple a s d)
# (triple q a z)
# (triple q w e)
#
# (shift G ^q) returns a new Graph
#
# (triple a z nil)
# (triple w e nil)
#
# If the object of a triple refers to a subject in the graph, 
# the shift makes that a sub-graph in the result ie
#
# var G2 (graph ^(q w e) ^(a s d) ^(q j a))
#
# (triple a s 1)
# (triple a s 2)
# (triple q j a)
# (triple q w e)
#
# (shift G2 ^q) returns 
#
# (triple j a (graph (triple a s 1) (triple a s 2)))
# (triple w e nil)
#
# This allows descent of a tree in the graph, so
#
# (shift (shift G2 ^q) ^j) returns
#
# (triple a s 2)
# (triple a s 1)
#
# and (shift (shift (shift G2 ^q) ^j) ^a) returns
#
# (triple s 1 nil)
# (triple s 2 nil)
#
# and maybe (shift (shift (shift (shift G2 ^q) ^j) ^a) ^s) may return
#
# (1 nil nil)
# (2 nil nil)
#
# or better
#
# (1 2)
#
# With shorthand syntax ; the expression (shift (shift (shift (shift G2 ^q) ^j) ^a) ^s) may become
#
#  G2;^q;^j;^a;^s
#

# given a graph `alpha` containing
#
#     sandnes
#        home-directory ('ports/Sandnes')
#        tsb:LOCODE (NOSAS)
#        tsb:latitude (58.8692)
#        tsb:longitude (5.7526)
#        name ('Sandnes')
#        type (tsb:port)
#        webcam (webcam020 webcam030 webcam010)
#     webcam010
#        interval (1)
#        location (stavanger)
#        type (html-stills-multi)
#        url ('https://www.meteoblue.com/en/weather/webcams/stavanger_norway_3137115')
#        webcam-id (1420931126 1656509771)
#     webcam020
#        interval (1)
#        location (sandnes)
#        type (stills-multi)
#        url ('https://www.2sandnessjo.no/webcam/webcam%a.jpg')
#        webcam-id (1)
#
# alpha;sandnes gives this graph:
#
#     home-directory
#        ports/Sandnes (nil)
#     tsb:LOCODE
#        NOSAS (nil)
#     tsb:latitude
#        58.8692 (nil)
#     tsb:longitude
#        5.7526 (nil)
#     name
#        Sandnes (nil)
#     type
#        tsb:port (nil)
#     webcam
#        webcam010 ((graph (triple webcam010 interval 1) (triple webcam010 location stavanger) (triple webcam010 type html-stills-multi) (triple webcam010 url 'https://www.meteoblue.com/en/weather/webcams/stavanger_norway_3137115') (triple webcam010 webcam-id 1420931126) (triple webcam010 webcam-id 1656509771)))
#        webcam020 ((graph (triple webcam020 interval 1) (triple webcam020 location sandnes) (triple webcam020 type stills-multi) (triple webcam020 url 'https://www.2sandnessjo.no/webcam/webcam%a.jpg') (triple webcam020 webcam-id 1)))
#        webcam030 (nil)
#
# alpha;sandnes;home-directory returns this graph:
#
#     ports/Sandnes
#        nil (nil)
#
# alpha;sandnes;webcam returns this graph:
#
#     webcam010
#        interval (1)
#        location (stavanger)
#        type (html-stills-multi)
#        url ('https://www.meteoblue.com/en/weather/webcams/stavanger_norway_3137115')
#        webcam-id (1420931126 1656509771)
#     webcam020
#        interval (1)
#        location (sandnes)
#        type (stills-multi)
#        url ('https://www.2sandnessjo.no/webcam/webcam%a.jpg')
#        webcam-id (1)
#     webcam030
#        nil (nil)
#
# and alpha;sandnes;webcam;webcam020 returns this graph:
#
#     interval
#        1 (nil)
#     location
#        sandnes (nil)
#     type
#        stills-multi (nil)
#     url
#        https://www.2sandnessjo.no/webcam/webcam%a.jpg (nil)
#     webcam-id
#        1 (nil)


@ns date "http://www.genyris.org/lang/date#"
@ns ntfy 'http://ntfy.sh/api'
@ns shell 'http://genyris.org/shell'
@ns sys 'http://www.genyris.org/lang/system#'
@ns task 'http://www.genyris.org/lang/task#'
@ns u "http://www.genyris.org/lang/utilities#"

@ns tsb "http://www.genyris.org/test-square-bracket#"

include 'square-bracket.g'

def line(l &rest msg)
    u:format '%s --------%a--------\n' l
        cond
            (null? msg) ""
            else msg

def graph-pretty-print (grph)
    cond
        (null? grph)
            print 'nil'
        else
            for S in (grph(.subjects))
                u:format '%s\n' S
                for P in (grph(.predicates S))
                    u:format '   %a %s\n' P (grph(.get-list S P))

var graph-alpha (graph)
graph-alpha
    .put ^sofd ^type ^tsb:liner
    .put ^sofd ^tsb:name 'Spirit of Discovery'
    .put ^sofd ^tsb:IMO 9802683
    .put ^sofd ^tsb:MMSI 232021171
    .put ^sofd ^tsb:fleet ^Saga
    .put ^sofd ^tsb:callsign ^MEYE7

    .put ^flam ^name 'Flam'
    .put ^flam ^type ^tsb:port
    .put ^flam ^tsb:latitude 60.8371
    .put ^flam ^tsb:longitude 7.1219
    .put ^flam ^tsb:LOCODE ^NOFLA
    .put ^flam ^home-directory ('%a/Flam'(.format sys:script-directory))
    .add (triple ^flam ^webcam ^webcam001)

    .put ^webcam001 ^interval 5
    .put ^webcam001 ^location ^flam
    .put ^webcam001 ^type ^stills
    .put ^webcam001 ^url 'https://webcam.flam.no/flam/flam15.jpg'

    .put ^stavanger ^name 'Stavanger'
    .put ^stavanger ^type ^tsb:port
    .put ^stavanger ^tsb:latitude 58.97177
    .put ^stavanger ^tsb:longitude 5.73677
    .put ^stavanger ^tsb:LOCODE ^NOSVG
    .put ^stavanger ^webcam ^webcam010
    .put ^stavanger ^home-directory ('%a/Stavanger'(.format sys:script-directory))

    .put ^webcam010 ^type ^html-stills-multi
    .put ^webcam010 ^location ^stavanger
    .put ^webcam010 ^interval 1
    .put ^webcam010 ^url 'https://www.meteoblue.com/en/weather/webcams/stavanger_norway_3137115'
    .add (triple ^webcam010 ^webcam-id 1656509771)
    .add (triple ^webcam010 ^webcam-id 1420931126)

    .put ^sandnes ^name 'Sandnes'
    .put ^sandnes ^type ^tsb:port
    .put ^sandnes ^tsb:latitude 58.8692
    .put ^sandnes ^tsb:longitude 5.7526
    .put ^sandnes ^tsb:LOCODE ^NOSAS
    .add (triple ^sandnes ^webcam ^webcam010)
    .add (triple ^sandnes ^webcam ^webcam010)
    .add (triple ^sandnes ^webcam ^webcam030)
    .add (triple ^sandnes ^webcam ^webcam020)
    .put ^sandnes ^home-directory 'ports/Sandnes'

    .put ^webcam020 ^type ^stills-multi
    .put ^webcam020 ^location ^sandnes
    .put ^webcam020 ^interval 1
    .put ^webcam020 ^url 'https://www.2sandnessjo.no/webcam/webcam%a.jpg'
    .add (triple ^webcam020 ^webcam-id 1)

graph-pretty-print graph-alpha


print`(graph-alpha;s)
print (graph-alpha(.select ^sandnes nil nil))

def shift ((g = Graph)(s = Symbol))
    var result (graph)
    var top (g(.select s nil nil))
    print (list @LINE top)
    cond
        (equal? top (triple nil nil nil)) (setq result nil)
        (equal? 0 (top(.length))) (setq result nil)
        (equal? 1 (top(.length)))
            var leaf (left(top(.asTriples)))
            cond
                (and (null? leaf;predicate) (null? leaf;object)) (setq result leaf;subject)
                else (setq result (left(top(.asTriples))))
        else
            for T in top
                print (list @LINE T)
                cond
                    (null? T;object)
                        result
                            .add T;predicate (intern T;object) nil
                    else
                        cond
                            (is-instance? T;object Graph)
                                setq result
                                    result(.union (T;object(.select T;predicate nil nil)))
                            else
                                var go (g(.select T;object nil nil))
                                cond
                                    (equal? 0 (go(.length)))
                                        result
                                            .add T;predicate (intern T;object) nil
                                    else
                                        result
                                            .add T;predicate (intern T;object) go
    result

def shift2 ((g = Graph)(s = Symbol))
    var result (graph)
    var top (g(.select s nil nil))
    print (list @LINE top)
    cond
        (equal? top (triple nil nil nil)) (setq result nil)
        (equal? 0 (top(.length))) (setq result nil)
        (equal? 1 (top(.length)))
            var leaf (left(top(.asTriples)))
            cond
                (and (null? leaf;predicate) (null? leaf;object)) (setq result leaf;subject)
                else (setq result (left(top(.asTriples))))
        else
            for T in top
                #print (list @LINE T)
                cond
                    (null? T;object)
                        result
                            .add T;predicate nil nil
                    else
                        cond
                            (is-instance? T;object Graph)
                                setq result
                                    result(.union (T;object(.select T;predicate nil nil)))
                            else
                                result
                                    .add T;predicate T;subject (intern T;object)
    result

var G (graph ^(q w e) ^(a s d) ^(q a z))
for T in G (print T)
line @LINE
for T in (shift G ^q) (print T) 
line @LINE
var G2 (graph ^(q w e) ^(q j a) ^(a s 1) ^(a s 2))
line @LINE
for T in G2 (print T)
line @LINE
for T in (shift G2 ^q) (print T)
line @LINE
print (shift (shift G2 ^q) ^j)
line @LINE
for T in (shift (shift G2 ^q) ^j) (print T)
line @LINE
for T in (shift (shift (shift G2 ^q) ^j) ^a) (print T)
line @LINE
for T in (shift (shift (shift (shift G2 ^q) ^j) ^a) ^s) (print T)
line @LINE
for T in (shift (shift (shift (shift (shift G2 ^q) ^j) ^a) ^s) (intern 1))(print T)

line @LINE '----one-graph-alpha;sandnes----------------------------\n'
var one
     shift graph-alpha ^sandnes
graph-pretty-print one
line @LINE '----two-graph-alpha;sandnes;home-directory----------------------------\n'
var two
    shift one ^home-directory
graph-pretty-print two

line @LINE '----three-graph-alpha;sandnes;webcam----------------------------\n'
var three
    shift one ^webcam
graph-pretty-print three

line @LINE '----four-graph-alpha;sandnes;webcam;webcam020----------------------------\n'
var four
    shift three ^webcam020
graph-pretty-print four

line @LINE '----five-graph-alpha;sandnes;webcam;webcam020;location----------------------------\n'
var five
    shift four ^location
graph-pretty-print five


#setq pling shift
#graph-pretty-print
#    graph-alpha;sandnes