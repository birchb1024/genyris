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
    print request
    cond 
        (equal? (request(.getPath)) '/favicon.ico')
            list ^SERVE-FILE script-dir (request(.getPath)) ^ls
        else
            setq counter (+ counter 1)
            list 200 "text/html"
              template
                  html()
                     head()
                        title() $script
                        style()
                            verbatim() "table, th, td { border: 1px solid black; border-collapse: collapse; }"
                     body()
                        dic()
                            'Script: ' $script 
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
  (equal? (task:id)!name 'main')
    print script
    httpd 8000 script
    u:format "Server listening on http://127.0.0.1:8000/\nType Ctrl-C to halt."
    read
