@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"
         
define forever (power 2 62)

define script
    (File(.new @FILE))
        .abs-path

define script-dir
   "The same as (script(.dirname))"
    '/'
        .join 
            reverse
                right
                    reverse
                        script (.split '/')

df httpd-serve (request)
   print request
   cond 
      (equal? (request(.getPath)) '/favicon.ico')
         list ^SERVE-FILE script-dir (request(.getPath)) ^ls
      else
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
         u:format "Server listening on http://127.0.0.1:8000/\n"
         sleep forever
