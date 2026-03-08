#
#
#

@ns date "http://www.genyris.org/lang/date#"
@ns ntfy 'http://ntfy.sh/api'
@ns shell 'http://genyris.org/shell'
@ns sys 'http://www.genyris.org/lang/system#'
@ns task 'http://www.genyris.org/lang/task#'
@ns u "http://www.genyris.org/lang/utilities#"

@ns tsb "http://www.genyris.org/test-square-bracket#"

include 'square-bracket.g'

def graph-pretty-print (grph)
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
    .put ^stavanger ^home-directory 'ports/Stavanger'

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
    .add (triple ^sandnes ^webcam ^webcam030)
    .add (triple ^sandnes ^webcam ^webcam020)
    .put ^sandnes ^home-directory 'ports/Sandnes'

    .put ^webcam020 ^type ^stills-multi
    .put ^webcam020 ^location ^sandnes
    .put ^webcam020 ^interval 1
    .put ^webcam020 ^url 'https://www.2sandnessjo.no/webcam/webcam%a.jpg'
    .add (triple ^webcam020 ^webcam-id 1)

#graph-pretty-print graph-alpha

assertEqual
    graph-alpha[]
    ^(flam sandnes sofd stavanger webcam001 webcam010 webcam020)

assertEqual
    graph-alpha[^nothing]
    nil

assertEqual
    graph-alpha[^sandnes]
    ^((home-directory = 'ports/Sandnes') (webcam = webcam020) (webcam = webcam030) (tsb:LOCODE = NOSAS) (tsb:longitude = 5.7526) (tsb:latitude = 58.8692) (type = tsb:port) (name = 'Sandnes'))

assertEqual
    graph-alpha[^sandnes ^webcam]
    ^(webcam020 webcam030)

assertEqual
    list
        triple ^flam ^webcam ^webcam001
    sort
        (graph-alpha[nil ^webcam ^webcam001])(.asTriples)

assertEqual
    list (triple ^flam ^webcam ^webcam001) (triple ^sandnes ^webcam ^webcam020) (triple ^sandnes ^webcam ^webcam030) (triple ^stavanger ^webcam ^webcam010)
    sort
        (graph-alpha[nil ^webcam nil])(.asTriples)

var an-assoc
    tag Alist ^((a = 1)(s = 2)(d = 3)(f = 4)(d = 4))
assertEqual nil (an-assoc[^notthere])
assertEqual 2 (an-assoc[^s])
assertEqual 3 (an-assoc[^d])


var a-list ^(z x c v)
assertEqual ^z (a-list[0])
assertEqual ^c (a-list[2])
catch err
    (a-list[22])
assert err

#print ^z (a-list[])

# assertEqual
#     Pair!slice ^(a b c d e f g) 2 5
#     ^(c d e)

