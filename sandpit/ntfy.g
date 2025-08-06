#
# Client library for ntfy.sh notification service (https://docs/ntfy.sh)
#
#
@ns ntfy 'http://ntfy.sh/api'
@ns web  "http://www.genyris.org/lang/web#"

# Configure the next line for custom topics
var ntfy:topic-prefix 'http://ntfy.sh/ATISYMML'

# Send a message to the subscribers with sub-topic 'topic'
def ntfy:post(topic msg)
    var response nil
    catch err
        setq response
            web:post ('%a%a' (.format ntfy:topic-prefix topic)) msg ^(('Content-Type' = 'text/plain'))
    cond
        err
            stderr(.format 'ERROR in web:post %s' err)
    response

# Test with:
# var response (ntfy:post 'RAW' ^WWWWW)
# print response
#( left response)
#    .copy stdout
