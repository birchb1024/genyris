@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"

         
df httpd-serve (request)
#   print request
    cond 
        (equal? (request(.getPath)) '/favicon.ico')
            list 404 "text/plain" ^("Not Found")
        else
           list 200 "text/html"
              template
                  html()
                     head()
                     body()
                        div()
                            "username: foo"
                        div()
                            "password: bar"
                        ul()
                            $(web-tasks)
def web-tasks()
    map-left (task:ps)
        lambda (x) 
            template
                li()
                    a
                        (href = $("http://127.0.0.1:%a/"(.format (must-split x!name 1))))
                        $(must-split x!name 2)


def must-split (item index)
    var splitted (item(.split))
    cond
        (> (length splitted) index)
            nth index splitted
        else 
            ''

cond
    (equal? (task:id)!name 'main')
        httpd 8000 @FILE
        httpd 8001 'examples/www-demo.g'
        httpd 8002 'examples/www-basic-auth.g'
        httpd 8003 'examples/www-hello.g'
        httpd 8004 'examples/www-ntlm.g'
        httpd 8005 'examples/www-rcepl.g'
        httpd 8006 'examples/www-static.g'
        httpd 8007 'examples/web-server.g' '8007' 'examples'
        httpd 8008 'examples/apps/inspector/inspect.g'
        u:format "Server listening on http://127.0.0.1:8000/\nType Ctrl-C to halt."
        read
