@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"
@prefix : "http://www.genyris.org/lang/types#"
@prefix render "http://www.genyris.org/lib/render#"
@prefix foaf 'http://xmlns.com/foaf/0.1/'

include "lib/types.g"
include "lib/types-render.g"
include "lib/classify.g"

include "examples/people.g"

define script
    (File(.new @FILE))
        .abs-path

define script-dir
    '/'
        .join 
            reverse
                right
                    reverse
                        script (.split '/')

Bignum
  def .new(obj)
     define parser (ParenParser(.new obj))
     parser(.read)

StandardClass
    def .render:html(this)
        template
            li()
                $(asString this!classname)

class DemoString(String)
  def .valid? (obj)
      is-instance? obj String

class DemoURL(DemoString)
    def .valid? (obj)
        obj(.regex 'http://[^/]+/.*')
    defmethod .render:html()
        template
            a((href=$this))
                $this

class DemoEmail(DemoString)
    def .valid? (obj)
        obj(.regex '[^@]+\@[^@]+')
    defmethod .render:html()
        template
            a((href=$('mailto:'(.+ this))))
                $this

def >< (min max x)
    # Number x value between min and max.
    and
        > x min
        < x max
       
class DemoDate(DemoString)
  define separator '/'
  def .valid? (obj)
      and
        obj(.regex '[0-9]{1,2}/[0-9]{1,2}/[0-9]+')
        > (obj(DemoDate!year)) 0
        >< 0 13 (obj(DemoDate!month))
        >< 0 32 (obj(DemoDate!day))
       
  def partAsNumber(this index)
        Bignum!new
            nth index (this(.split separator))
  def .year()
        partAsNumber .self 2
  def .month()
        partAsNumber .self 1
  def .day()
        partAsNumber .self 0

def parse_string (string)
    " parse a string returning the expression "
    (IndentedParser(.new string))
        .prefix ^sys "http://www.genyris.org/lang/system#"
        .prefix ^u "http://www.genyris.org/lang/utilities#"
        .prefix ^java 'http://www.genyris.org/lang/java#'
        .prefix ^foaf 'http://xmlns.com/foaf/0.1/'
        .prefix ^type 'http://www.genyris.org/lang/types#'
        .read

define root_classes (list :Record :SequenceOfRecords DemoString foaf:Person)

define context (dict)

def global-eval(exp)
    context
        eval exp

define counter 0
df httpd-serve (request)
    setq counter (+ counter 1)
    define input_line  
        (request (.getParameters))
            .lookup 'recr'
    define expression nil
    catch err
        setq expression
#            global-eval (parse_string input_line)
            parse_string input_line
        map-left root_classes
            lambda (klass)
               classify klass expression
    cond
        err
            setq expression err

    cond 
        (equal? (request(.getPath)) '/favicon.ico')
            list ^SERVE-FILE script-dir (request(.getPath)) ^ls
        else
            list 200 "text/html"
               template
                   html()
                      head()
                         title() $sys:argv
                         style()
                             verbatim() "table, th, td { padding: 10px; border: 1px solid black; border-collapse: collapse; }"
                      body()
                         div()
                             form((action="posted") (method="post"))
                                 fieldset()
                                     legend()
                                         "Read-Eval-Classify-Render Form:"
                                     textarea
                                         (rows="10")
                                             cols="50"
                                             name="recr"
                                             wrap="physical"
                                         ~$input_line
                                     br()
                                     input((type="submit")(name="Submit")(value="submit"))
                         div()
                             'Classes: '
                             ul()
                                 $(map-left (use expression .classes) StandardClass!render:html)
                         div()
                             pre() 
                                 $(asString expression)
                         fieldset()
                             legend()
                                 "Render as HTML:"
                             $(catch err (expression(.render:html)))
                        

cond
  (equal? (task:id)!name 'main')
    httpd 8000 @FILE
    u:format "Server listening on http://127.0.0.1:8000/\nType Ctrl-C to halt."
    read
