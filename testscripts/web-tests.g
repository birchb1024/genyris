@prefix u   "http://www.genyris.org/lang/utilities#"
@prefix t   "http://www.genyris.org/lib/gunit#"
@prefix web "http://www.genyris.org/lang/web#"

include "testscripts/gunit.g"

def readPage(url)
    var wstream (web:get url)
    var count 0
    while (wstream(.hasData))
         count = (+ count 1)
         write (wstream(.read)) ^-
    wstream(.close)
    u:format "%nRead %a bytes%n" count
    count

def run-web()
    var thread (httpd 7777 "testscripts/www-text.g")
    sleep 1000
    thread(.kill)

def run-web-get()
    var thread (httpd 7778 "testscripts/www-text.g")
    sleep 1000
    var result (readPage "http://localhost:7778/")
    thread(.kill)
    result

def run-web-static-get()
    var thread (httpd 7778 "testscripts/www-static.g" ".")
    sleep 1000
    var result (readPage "http://localhost:7778/LICENSE")
    thread(.kill)
    result

run-web
run-web-get
run-web-static-get
