#
# Convert an XML file to JSON
#
def xml2json (filename)
    var fd
       (File(.new filename))
          .open ^read
    var parser (XMLParser(.new fd))
    var result
          parser(.read)
    var outstream
        (File(.new ('%a.json'(.format filename))))
            .open ^write
    outstream
        .format '%j' result
        .close

def xml2lisp (filename)
    var fd
       (File(.new filename))
          .open ^read
    var parser (XMLParser(.new fd))
    var result
          parser(.read)
    var outstream
        (File(.new ('%a.g'(.format filename))))
            .open ^write
    outstream
        .format '%i' (cdr result)
        .close

def xml2html (filename)
    var fd
       (File(.new filename))
          .open ^read
    var parser (XMLParser(.new fd false))
    var result
          parser(.read)
    var outstream
        (File(.new ('%a.html'(.format filename))))
            .open ^write
    outstream
        .format '%x' (cdr result)
        .close

var files ^('catalog.xml' 'rootservices.xml' 'services.xml' 'types.xml')
for F in files
   xml2lisp ('/mnt/HomeDisk2/birchb/work/doors/%a'(.format F))
   xml2json ('/mnt/HomeDisk2/birchb/work/doors/%a'(.format F))
   xml2html ('/mnt/HomeDisk2/birchb/work/doors/%a'(.format F))



