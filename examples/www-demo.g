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
                style()
                    verbatim() "table, th, td { border: 1px solid black; border-collapse: collapse; }"
             body()
                div() 
                    "Hit number: " $counter
                div()
                    $(request (.toHTML))
                div()
                    a((href="/")) "GET link"
                    " "
                    a((href="/geted?variable=value&another=42")) "GET link with arguments"
                div()
                    form((action="posted") (method="post"))
                        fieldset()
                            legend()
                                "POST Form:"
                            "Text: " 
                            input((type="text")(name="Text")(value='text here'))
                            "Password: " 
                            input((type="password")(name="password")(value='secret'))
                            br()
                            "Checked: "
                            input((type="checkbox")(name="checkbox") (value="Checked"))    
                            input((type="radio")(name="radio") (value="yes") (checked="true"))
                                "Yes"
                            input((type="radio")(name="radio") (value="no"))
                                'No'
                            br()
                            input((type="submit")(name="Submit")(value="submit"))
                        

cond
  (and sys:argv (equal? (task:id)!name 'main'))
    httpd 8000 sys:argv!left
    u:format "Server listening on http://127.0.0.1:8000/\nType Ctrl-C to halt."
    read
