@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"

define counter 0
df httpd-serve (request)
   setq counter (+ counter 1)
   print request
   list 200 "text/html"
      template
          html()
             head()
                title() $sys:argv
             body()
                div() "Hit number: " $counter
                pre() 
                    verbatim() $sys:argv
                $(request (.toHTML))


cond
  (and sys:argv (equal? (task:id)!name 'main'))
    httpd 8000 sys:argv!left
    u:format "Server listening on http://127.0.0.1:8000/\nType Ctrl-C to halt."
    read
