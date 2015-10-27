## Copyright 2014 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix u   "http://www.genyris.org/lang/utilities#"
@prefix web "http://www.genyris.org/lang/web#"
@prefix sys "http://www.genyris.org/lang/system#"
import csv
include 'lib/classify.g'

#
# Start with:
# task:httpd 80 'examples/triple-browser.g' 'http://<server>:<port>/<path to triples file>.csv'
#
#
#
define lastTime (now)
def logInfo (message)
  define timeNow (now)
  u:format "Info: %s %s\n" (- timeNow lastTime) message
  setq lastTime timeNow
logInfo "Start"
define store (graph)


def myparse (string)
   # parse a string returning the expression
   (ParenParser(.new string))
      .prefix "rdf" "http://w3c/rdf#"
      .prefix "rdfs" "http://w3c/rdfs#"
      .prefix "dw" "http://coles/data-warehouse#"
      .prefix "scm" "http://coles/scm#"
      .read

def tryToParseSymbol (str)
    # Parse a string as a Genyris Symbol, if parsing fails intern the raw string.
    cond
      (equal? 1 (length (str(.split))))
        define result
           catch errors (myparse str)
        if errors
            then
                print errors # output errors to stdout
                intern str
            result
      else
        intern str


def loadDataFromCSV(url)
    # Read and parse a file from the net using URL as input.
    var in nil
    cond
      (url(.match "^file:.*"))
        define filename (nth 1 (url(.split ':')))
        setq in 
          (File(.new filename))
            .open ^read
      else
        setq in (left (web:get url))
    define lines (csv!read in ',' '"')
    in (.close)
    logInfo "Starting load..."
    for aslist in lines
      cond
            (equal? (length aslist) 1)
                #u:format "triple error: %s - %s %a%n" (length aslist) line aslist
            else
                if (equal? (length aslist) 2)
                    define object ""
                    define object (nth 2 aslist)
                define subject (tryToParseSymbol (nth 0 aslist))
                define predicate (tryToParseSymbol (nth 1 aslist))
                catch derror
                   define tr (triple subject predicate object)
                cond
                   derror
                      u:format "triple error: %s %s %s %s%n" derror subject predicate object
                store (.add tr)
    logInfo "Finished loading."


class Query()
    #
    # Normal regex query
    #
    def .valid?(str)
        is-instance? str String
    defmethod .convertToRegex ()
        # Convert the user-supplied string to a regular expression.
        ".*" (.+ (this(.toLowerCase)) '.*')
    def .render() .self
    def .isEmpty?()
        or (null? .self) (equal? .self "")
    defmethod .match(other)
        ((asString other) (.toLowerCase))
            .match (this(.convertToRegex))

class NullQuery(Query)
    #
    # Empty query
    #
    def .valid?(str)
        or (null? str) (equal? str "")

        
class SymbolQuery(Query)
    #
    # Query wrapped in |pipes| which are to be ignored.
    #
    def .valid?(str)  
      str(.match '\\|.*\\|')

    defmethod .convertToRegex ()
      # Expressions enclosed in quotes are used as-is.
      ".*" (.+ (cadr ((this(.toLowerCase))(.split '\\|'))) ".*")

    defmethod .render()
      cadr (this(.split '\\|'))

    defmethod .match(other)
      ((asString other) (.toLowerCase))
          .match (this(.convertToRegex))

class ExactQuery(Query)
    #
    # Query wrapped in quotes.
    #
    def .valid?(str)
        str(.match '".*"')
    defmethod .convertToRegex ()
        # Expressions enclosed in quotes are used as-is.
        cadr (this(.split '"'))
    def .match(other)
        equal? (.convertToRegex) (asString other)
    defmethod .render()
        "&quot;" (.+ (this(.convertToRegex)) "&quot;") 
        

def searchStore(query)
  # Search the triplestore using the query.
  # return a Graph of results.
  def matchFunc(s o p)
      # Matching function for the search
      # is the string found anywhere in the triple?
      or
          query (.match s)
          query (.match p)
          query (.match o)
  store(.select nil nil nil matchFunc)

def renderSearchURL (str)
  # return an HTML rendering of the search page.
   "/?op=Search&query="(.+ str)

def renderTriple (t)
   # Render a result triple in HTML table rows.
   define s (asString(t(.subject)))
   define p (asString(t(.predicate)))
   cond
        (p (.match ".*_password.*"))
            define o "XXXXXXXXX"
        else
            define o (asString(t(.object)))
   template
      tr()
         td()
            a((href = $(renderSearchURL s))) $s
         td()
            a((href = $(renderSearchURL p))) $p
         td()
            a((href = $(renderSearchURL o))) $o

def renderTripleList (ts)
    # Render a list of triples as an HTML table.
    define result nil
    for trip in ts
        setq result (cons (renderTriple trip) result)
    template
      table()
          tr()
              td()
                  strong() "Subject"
              td()
                  strong() "Property"
              td()
                  strong() "Object"
              $result

def reloadFromSource ()
    # Reload the source data from the CSVURL
    # Print out some HTML for the page.
    loadDataFromCSV CSVURL
    print
      template
        "Reload from " $CSVURL
    template
       "Reloaded from "
          a((href = $CSVURL))
             $CSVURL

class AbstractRequest(HttpRequest)
    # Default, possibly Unknown request type...
    def .valid? (thing) true
    def .getQuery()
      tag Query "scm.APPLICATION"
    defmethod .do(message)
      # Search the triplestore and retrn the HTML results...
      define searchResults 
        (searchStore (.getQuery))
          .asTriples
      define renderedResults
        renderTripleList searchResults
      list 200 "text/html"
        template
            html()
               head()
                 title() "CMDB Search"
               body()
                  h2() "CMDB Search"
                  form()
                      p() "Enter a regular expression to search in keys and data (case insensitive). Surround query in quotes for an exact match. Click a link to search for the link's text."
                      input((name ="query") (size ="100") (value = $( (this(.getQuery))(.render)))) ""
                      verbatim() '&nbsp;&nbsp;&nbsp;'
                      input((type ="submit") (name ="op") (value ="Search"))
                      input((type ="submit") (name ="op") (value ="Reload"))
                      div() $message
                  div()
                      $renderedResults
class QueryRequest(AbstractRequest)
    # This class of request actually has an "op" filled in.
    def .valid? (req)
        equal? "Search" 
          (req(.getParameters))
            .lookup "op"
    defmethod .getQuery ()
      define query 
        (.getParameters)(.lookup "query")
      classify Query query
      the query 
          
class ReloadRequest(AbstractRequest)
    # When the user selects the Reload button
    # reload the data and send back the request page.
    def .valid? (req)
        equal? "Reload" 
          (req(.getParameters))
            .lookup "op"
    defmethod .getQuery ()
      define query 
        (.getParameters)(.lookup "query")
      classify Query query
      the query 
    def .do(message)
      AbstractRequest!do (reloadFromSource)


df httpd-serve (request)
  classify AbstractRequest request 
  request(.do "")

catch errors
  define CSVURL (nth 1 sys:argv)
if errors
   u:format 'Usage: %s missing URL to CSV file' sys:argv
   nil
loadDataFromCSV CSVURL