#
#
#
@prefix email "http://www.genyris.org/lang/email#"


catch error
   email:send ^('birchb@genyris.org') 'test message Subject' "Test message body" 'birchb@tpg.com.au' 'bogus'

print error
