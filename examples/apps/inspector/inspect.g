@prefix sys "http://www.genyris.org/lang/system#"

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


define context (dict)
def myparse (string)
   # parse a string returning the expression
   (ParenParser(.new string))
      .prefix "rdf" "http://w3c/rdf#"
      .prefix "rdfs" "http://w3c/rdfs#"
      .read

df httpd-serve (request)
    print request
    cond 
        (equal? (request(.getPath)) '/favicon.ico')
            list ^SERVE-FILE script-dir (request(.getPath)) ^ls
        else
           define expression nil
           define expression-string ''
           define result nil
           var params (request(.getParameters))
           catch eval-errors
                setq expression-string (params (.lookup "expression"))
                setq expression (myparse expression-string)
                setq result (context(eval expression))
           cond
              eval-errors
                 setq result (list '*** ERROR: ' eval-errors)
                 
           list 200 "text/html"
              template
                  html()
                     head()
                        title() "Genyris Inspector"
                     body()
                        form((action="/"))
                           input((type="hidden")(name="expression")(value="(include 'examples/apps/inspector/inspect.g')"))
                           input((type="submit")(value="reload"))
                        pre() $("%s"(.format expression))
                        verbatim() '<hr>'
                        pre() $("%s"(.format result))
                        verbatim() '<hr>'
                        form((action="/"))
                           input((type="textarea")(name="expression")(length="32"))
                           input((type="submit")(value="Eval"))


