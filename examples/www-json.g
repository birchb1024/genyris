@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"

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
define counter 0
df httpd-serve (request)
#    print request
    cond 
        (equal? (request(.getPath)) '/favicon.ico')
            list 404 "text/plain" "Not Found"
        else
            setq counter (+ counter 1)
            list 200 "application/json" 
                list counter request

cond
  (equal? (task:id)!name 'main')
    print script
    httpd 8888 script
    u:format "Server listening on http://127.0.0.1:8888/\nType Ctrl-C to halt."
    read
