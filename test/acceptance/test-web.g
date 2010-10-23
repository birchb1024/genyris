@prefix u   "http://www.genyris.org/lang/utilities#"
@prefix web "http://www.genyris.org/lang/web#"

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
    var thread (httpd 7776 "test/mocks/www-text.g")
    sleep 1000
    thread(.kill)

def run-web-get()
    var thread (httpd 7778 "test/mocks/www-text.g")
    sleep 1000
    var result (readPage "http://localhost:7778/")
    thread(.kill)
    result

def run-web-static-get()
    var thread (httpd 7778 "test/mocks/www-static.g" ".")
    sleep 1000
    var result (readPage "http://localhost:7778/LICENSE")
    thread(.kill)
    result

run-web
run-web-get
run-web-static-get
