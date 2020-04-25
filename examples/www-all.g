@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"

define forever (power 2 62)

define script
    (File(.new @FILE))
        .abs-path

define script-dir
    '/'
        .join 
            reverse
                right
                    reverse
                        script (.split '/')
         
df httpd-serve (request)
#   print request
    cond 
        (equal? (request(.getPath)) '/favicon.ico')
            list ^SERVE-FILE script-dir (request(.getPath)) ^ls
        else
           list 200 "text/html"
              template
                  html()
                     head()
                        title() $script
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
                        $(must-split x!name 2) '  ' $(must-split x!name 1)


def must-split (item index)
    var splitted (item(.split))
    cond
        (> (length splitted) index)
            nth index splitted
        else 
            ''

cond
    (equal? (task:id)!name 'main')
        for count in (range 1 1)
            httpd 8000 @FILE
            httpd 8001 'examples/www-demo.g'
            httpd 8002 'examples/www-basic-auth.g'
            httpd 8003 'examples/www-hello.g'
            httpd 8004 'examples/www-ntlm.g'
            httpd 8005 'examples/www-rcepl.g'
            httpd 8007 'examples/web-server.g' '8007' 'examples'
            httpd 8008 'examples/apps/inspector/inspect.g'
            httpd 8888 'examples/www-json.g'
        u:format "Server listening on http://127.0.0.1:8000/\n"
        sleep forever
