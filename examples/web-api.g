#
# Example
#
@prefix u   "http://www.genyris.org/lang/utilities#"
@prefix web "http://www.genyris.org/lang/web#"

var response (left (web:get 'https://api.ipify.org/?format=json'))
var theJSON (response(.readAll))
print theJSON
print (theJSON(.fromJSON))!ip

var response (left (web:get 'https://api.squiggle.com.au/?q=games;year=2018;complete=100'))
var decode (response(.readAll))
for game in ((decode(.fromJSON)).games)
    u:format "%a %a winner: %a %a %a\n" game!venue game!round game!winner game!ateam game!hteam

# http://www.bom.gov.au/fwo/IDV60901/IDV60901.95936.json
var response (left (web:get 'http://www.bom.gov.au/fwo/IDV60901/IDV60901.95936.json'))
var decode (response(.readAll))
for location in (((decode(.fromJSON)).observations).data)
    u:format "%a %a\n" location!local_date_time (scale (location!air_temp) 2) 

#
        
      
        

