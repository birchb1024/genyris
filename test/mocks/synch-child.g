@prefix sys "http://www.genyris.org/lang/system#"
@prefix u "http://www.genyris.org/lang/utilities#"
@prefix task "http://www.genyris.org/lang/task#"

u:format "Child: %s\n" sys:argv
var shared (nth 1 sys:argv)
var start (now)
while (> (+ start 5000) (now))
#    u:format "Child: %s\n" shared!left
    task:synchronized shared
#        u:format "Child in\n"
        shared (setq .left 1)
        sleep 13
        shared (setq .left 0)
        sleep 23 
#    u:format "Child out %s\n" shared