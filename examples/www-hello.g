df httpd-serve (request)
   print request
   list 200 "text/html"
      template
          html()
             head()
             body()
                h1()
                  "Hello World"

