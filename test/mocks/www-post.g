#
# Server to return POST request parameters
#
@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"

df httpd-serve (request)
   list 200 "text/plain"
        request(.getParameters)

cond
  (and sys:argv (equal? (task:id)!name 'main'))
    httpd 8000 sys:argv!left
    u:format "Server listening on http://127.0.0.1:8000/\nType Ctrl-C to halt."
    read
