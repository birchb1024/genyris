@prefix sys "http://www.genyris.org/lang/system#"
define context (dict)
def myparse (string)
   # parse a string returning the expression
   (ParenParser(.new string))
      .prefix "rdf" "http://w3c/rdf#"
      .prefix "rdfs" "http://w3c/rdfs#"
      .read

df httpd-serve (request)
   print request
   var params (request(.getParameters))
   define expression-string (params (.lookup "expression"))
   define expression (myparse expression-string)
   define result nil
   catch eval-errors
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
                pre() ,("%s"(.format expression))
                verbatim() '<hr>'
                pre() ,("%s"(.format result))
                verbatim() '<hr>'
                form((action="/"))
                   input((type="textarea")(name="expression")(length="32"))
                   input((type="submit")(value="Eval"))


