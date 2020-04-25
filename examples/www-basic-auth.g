@prefix sys "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"
@prefix task "http://www.genyris.org/lang/task#"

define forever (power 2 62)

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

def valid-logon?(username password)
   # redefine to suit
   (and (equal? username 'foo') (equal? password 'bar')) 

def authenticate(request)
   #  Check the username and password
   var userpass (request (.getBasicUsernamePassword))
   cond
        (valid-logon? userpass!username userpass!password) userpass
        else nil

df httpd-serve (request)
   # Serve web page, catch an errors and report with backtrace.
   cond 
      (equal? (request(.getPath)) '/favicon.ico')
         list ^SERVE-FILE script-dir (request(.getPath)) ^ls
      else
         var response nil
         catch errors
              setq response (raw-serve request)
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
                      p() "*** Error - " $errors
                      p() $bt
                      div() $(request (.toHTML))

def raw-serve (request)
   # basic page
   var userpass (authenticate request)
   cond
      userpass
         u:format "%a Request from %a for %s\n" (u:getLocalTime) userpass!username (request(.getPath))
         handle-authenticated-request request userpass!username
      else
         u:format "%a Invalid basic auth in %s\n" (u:getLocalTime) (request (.getBasicUsernamePassword))
         login request

def handle-authenticated-request(request username)
   # success page-override as needed.
   list 200 'text/html'
            template
                html()
                   head()
                      title() "Basic Auth demo"
                   body()
                      p() $('Welcome %s, you are authorized.' (.format username))
                      $(request (.toHTML))

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
cond
    (equal? (task:id)!name 'main')
         httpd 8000 @FILE
         u:format "Server listening on http://127.0.0.1:8000/\n"
         sleep forever
