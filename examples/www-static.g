#
# This example demonstrates a web server with basic authentication, serving static web pages.
# It uses the SERVE-FILE directive.
#

@prefix u "http://www.genyris.org/lang/utilities#"

include 'examples/www-basic-auth.g'

var *users* (graph)
*users*
    .add 
       triple ^user1 ^hasPassword ^pw1
    .add 
       triple ^user2 ^hasPassword ^pw2

def valid-logon?(username password)
    equal? 1
        (*users* (.select (intern username) ^hasPassword (intern password)))(.length)


def handle-authenticated-request (request username)
   list ^SERVE-FILE '/' (request(.getPath)) ^ls

# Run using:
# var server (httpd 80 'examples/www-static.g')
#
