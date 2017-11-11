@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"
         
df httpd-serve (request)
   print request
   list 200 "text/html"
      template
          html()
             head()
             body()
                h1()
                  "Hello World"
cond
    (equal? (task:id)!name 'main')
         httpd 8000 @FILE
         u:format "Server listening on http://127.0.0.1:8000/\nType Ctrl-C to halt."
         read
