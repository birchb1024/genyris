#
# Example
#
@prefix web "http://www.genyris.org/lang/web#"

var response (left (web:get 'https://api.ipify.org/?format=json'))
var decode (response(.readAll))
print (decode(.fromJSON))!ip
