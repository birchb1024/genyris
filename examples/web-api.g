#
# Example
#
@prefix u   "http://www.genyris.org/lang/utilities#"
@prefix web "http://www.genyris.org/lang/web#"

def get-response-body(url)
    var response (web:get url)
    assert response
    assert (equal? 200 ((nth 2 response)(.left)))
    response
        .left
            .readAll
    
var theJSON (get-response-body 'https://api.ipify.org/?format=json')
u:format "My IP is %a\n" (theJSON(.fromJSON))!ip

var decode (get-response-body 'https://api.squiggle.com.au/?q=games;year=2018;complete=100')
for game in ((decode(.fromJSON)).games)
    u:format "%a %a winner: %a %a %a\n" game!venue game!round game!winner game!ateam game!hteam

var decode (get-response-body 'http://www.bom.gov.au/fwo/IDV60901/IDV60901.95936.json')
for location in (((decode(.fromJSON)).observations).data)
    u:format "%a %a\n" location!local_date_time (scale (location!air_temp) 2) 

#
        
      
        

