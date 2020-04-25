@prefix sys "http://www.genyris.org/lang/system#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix u   "http://www.genyris.org/lang/utilities#"

include 'classify.g'

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


class ListOfInts()
    defmethod .beginsWith?(other)
       beginsWith? other this
    defmethod .word(n)
       (+ (nth n this) (* 256 (nth (+ n 1) this)))
    defmethod .slice(start end) # todo slow?
       var result nil
       for i in (range start end)
           setq result (append result (list (nth i this)))
       result
    defmethod .get-utf-16(offset length)
       String!fromInts 'UTF-16LE' (.slice offset (+ offset (- length 1)))
    defmethod .get-chars(charset offset length)
       String!fromInts charset (.slice offset (+ offset (- length 1)))

class UncheckedRequest(HttpRequest)
    def .valid?(request) true #TODO
    def .reply()
        list '401 Unauthorized' 
            data
               'Content-Type' = "text/plain"
               'WWW-Authenticate' = "NTLM"
               'Connection' = 'Keep-Alive'
            ~ "401 Login Required"

class FaviconRequest(UncheckedRequest)
    def .valid?(request)
        equal? (request(.getPath)) '/favicon.ico'
    def .reply()
        list '404 Not Found' ^(('Content-Type' = "text/html")) "404 Not Found"
        
class UnauthorizedRequest(UncheckedRequest)
    def .valid?(request)
        null? (request(.getAuthorizationHeader))
    def .reply()
        list '401 Unauthorized' 
            data
               'Content-Type' = "text/plain"
               'WWW-Authenticate' = "NTLM"
               'Connection' = 'Keep-Alive'
            ~ "401 Login Required"


class NtlmRequest(UncheckedRequest)
    def .valid?(request)
        define auth-header (request(.getAuthorizationHeader))
        cond
            auth-header
                cond
                    (equal? 'NTLM' (auth-header(.slice 0 3)))
                        NtlmAuthorizationInts!valid? (.getAuthAsInts (.getAuthBase64 request))
                
    def .getAuthBase64(request)
        (request(.getAuthorizationHeader))(.slice 5 -1)
        
    def .getAuthAsInts(str)
       tag ListOfInts
          str(.fromBase64asUnsignedIntegers)
    defmethod .reply()
        define auth-ints (.getAuthAsInts (.getAuthBase64 this))
        classify NtlmAuthorizationInts auth-ints
        print
            auth-ints(.decode)
        auth-ints(.ntlmResponse this)

class EstablishedNtlmRequest(UncheckedRequest)
    def .valid?(request)
        define sessionID (request(.getSessionID))
        member? sessionID authenticatedSessions
                
    defmethod .reply()
       list '200 OK' ^(('Content-Type' = "text/plain")('Connection' = 'Keep-Alive')) 
            "session id = %a"
               .format (this (.getSessionID))

class NtlmAuthorizationInts(ListOfInts)
    def .getType(list-of-ints)
        nth 8 list-of-ints
    def .valid?(list-of-ints)
        list-of-ints
             .beginsWith? ('NTLMSSP'(.toInts 'ASCII'))
    def .ntlmResponse(request)
       list '200 OK' ^(('Content-Type' = "text/plain")('Connection' = 'Keep-Alive')) (.getType)


class NtlmAuthorizationType1(NtlmAuthorizationInts)
    def .valid?(list-of-ints)
       equal? 1 (.getType list-of-ints)
    def .ntlmResponse(request)
       define raw-response 
          tag ListOfInts ^(78 84 76 77 83 83 80 0 2 0 0 0 0 0 0 0 40 0 0 0 1 130 0 0 83 114 118 78 111 110 99 101 0 0 0 0 0 0 0 0)
       define response 
            "NTLM %a"
                .format (Base64!encodeUnsignedIntegers raw-response)
       list '401 Unauthorized' 
            list ^('Content-Type' = "text/plain") ^('Connection' = 'Keep-Alive')
                cons 'WWW-Authenticate' response
            ~ "401 Login Required" 
    defmethod .decode()
       var result (dict)
       result
           define .type 1
           define .domain-length (this(.word 16))
           define .domain-offset (this(.word 20))
           define .host-length (this(.word 24))
           define .host-offset (this(.word 28))
           define .domain (this (.get-chars 'ASCII' result!domain-offset result!domain-length))
           define .host (this (.get-chars 'ASCII' result!host-offset result!host-length))
       result  

define authenticatedSessions nil

class NtlmAuthorizationType3(NtlmAuthorizationInts)
    def .valid?(list-of-ints)
       equal? 3 (.getType list-of-ints)
    defmethod .decode()
       var result (dict)
       result
           define .type 3
           define .domain-length (this(.word 28))
           define .domain-offset (this(.word 32))
           define .user-length (this(.word 36))
           define .user-offset (this(.word 40))
           define .host-length (this(.word 44))
           define .host-offset (this(.word 48))
           define .domain (this (.get-utf-16 result!domain-offset result!domain-length))
           define .host (this (.get-utf-16 result!host-offset result!host-length))
           define .username (this (.get-utf-16 result!user-offset result!user-length))
       #u:format "domain: %a host: %a user: %a\n" 
            #.get-utf-16 result!domain-offset result!domain-length
            #.get-utf-16 result!host-offset result!host-length
            #.get-utf-16 result!user-offset result!user-length
       result  
    def .ntlmResponse(request)
       define sessionID (request(.getSessionID))
       setq authenticatedSessions (cons sessionID authenticatedSessions)
       list '200 OK' ^(('Content-Type' = "text/plain")('Connection' = 'Keep-Alive')) 
            (.decode)
                .self

          
        
df httpd-serve (request)
   cond 
      (equal? (request(.getPath)) '/favicon.ico')
         list ^SERVE-FILE script-dir (request(.getPath)) ^ls
      else
        request
            print .self
            classify UncheckedRequest .self
            define response (.reply)
            print response
            response

cond
    (equal? (task:id)!name 'main')
         httpd 8000 @FILE
         u:format "Server listening on http://127.0.0.1:8000/\n"
         sleep forever   
