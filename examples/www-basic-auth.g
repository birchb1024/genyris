@prefix sys "http://www.genyris.org/lang/system#"

def authenticate(request)
   var headers (request(.getHeaders))
   cond
      (not (headers (.hasKey 'authorization'))) nil
      (equal? (headers(.lookup 'authorization')) 'Basic Zm9vOmJhcg==') true
      else nil

df httpd-serve (request)
   print request
   cond
      (authenticate request)
         list 200 'text/html'
            template
                html()
                   head()
                      title() "Basic Auth demo"
                   body()
                      p() "Authorized"
                      ,(request (.toHTML))
      else
           login request

def login(request)
   var response
      list 401 ^(('Content-Type' = "text/plain")('WWW-Authenticate' = 'Basic realm="Secure Area"')) "Login Required"
   print response
   response

