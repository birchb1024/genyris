@prefix sys "http://www.genyris.org/lang/system#"

var *user* 'foo'
var *password* 'bar'

def authenticate(request)
   #  Check the username and password
   var userpass (request (.getBasicUsernamePassword))
   cond
        (and (equal? userpass!username *user*) (equal? userpass!password *password*)) 
           userpass
        else nil

df httpd-serve (request)
   # Serve web page, catch an errors and report with backtrace.
   var response nil
   catch errors
        response = (raw-serve request)
   cond
       (not errors) response
       else
           backtrace request errors

def backtrace (request errors)
   # Error page
   var bt (sys:backtrace)
   list 500 'text/html'
       template
            html()
               head()
                      title() "Internal error"
               body()
                      p() "*** Error - " ,errors
                      p() ,bt
                      div() ,(request (.toHTML))

def raw-serve (request)
   # basic page
   var userpass (authenticate request)
   cond
      userpass
         echo request userpass!username
      else
           login request

def echo(request username)
   # success page
   list 200 'text/html'
            template
                html()
                   head()
                      title() "Basic Auth demo"
                   body()
                      p() ,('Welcome %s, you are authorized.' (.format username))
                      ,(request (.toHTML))

def login(request)
   # Needs auth page
   var response
      list 401 ^(('Content-Type' = "text/html")('WWW-Authenticate' = 'Basic realm="Secure Area"')) "401 Login Required"
   response

# Run using:
# var server (httpd 80 'examples/www-basic-auth.g')
#
# web:get 'http://localhost/' (list (cons 'authorization' ('Basic %a' (.format ('foo:bar'(.toBase64))))))
#
