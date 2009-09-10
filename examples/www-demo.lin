@prefix sys "http://www.genyris.org/lang/system#"
         
df httpd-serve (request)
   print request
   list 200 "text/html"
      template
          html()
             head()
                title() "Genyris demo"
             body()
                pre() ,sys:argv
                ,(request (.toHTML))

