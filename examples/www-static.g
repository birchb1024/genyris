#
# This example demonstrates a web server with basic authentication, serving static web pages.
# It uses the SERVE-FILE directive.
#

@prefix u "http://www.genyris.org/lang/utilities#"
@prefix task "http://www.genyris.org/lang/task#"
@prefix sys "http://www.genyris.org/lang/system#"

include 'examples/www-basic-auth.g'

var *users* (graph)
*users*
    .add 
       triple ^demo ^hasPassword ^demo
    .add 
       triple ^foo ^hasPassword ^bar
    .add 
       triple ^user1 ^hasPassword ^pw1
    .add 
       triple ^user2 ^hasPassword ^pw2

def valid-logon?(username password)
    equal? 1
        (*users* (.select (intern username) ^hasPassword (intern password)))(.length)


def handle-authenticated-request (request username)
   list ^SERVE-FILE '/' (request(.getPath)) ^ls


cond
    (and sys:argv (equal? (task:id)!name 'main'))
         httpd 8000 @FILE
         u:format "Server listening on http://127.0.0.1:8000/\nType Ctrl-C to halt."
         read
