@prefix task "http://www.genyris.org/lang/task#"
@prefix u "http://www.genyris.org/lang/utilities#"

var shared (cons 0 0)
var child
    spawn 'testscripts/synch-child.g' shared
var start (now)
while (> (+ start 5000) (now))
    var read-value 0    
    task:synchronized shared
        read-value = (shared.left)        
    cond
        (equal? 1 read-value)
            child(.kill)
            raise "Parent error"
    sleep 131

