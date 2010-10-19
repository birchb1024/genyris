@prefix task "http://www.genyris.org/lang/task#"
@prefix u "http://www.genyris.org/lang/utilities#"

var shared (cons 0 0)
var child
    spawn 'testscripts/synch-child.g' shared
var start (now)
while (> (+ start 5000) (now))
    u:format "start=%s now=%s the %s\n" start (now) (the now)
    var read-value 0    
    task:synchronized shared
        u:format "Parent in\n"
        read-value = (shared.left)        
    u:format "Parent out\n"
    cond
        (equal? 1 read-value)
            child(.kill)
            raise "Parent error"
    sleep 131

