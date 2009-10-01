@prefix sys "http://www.genyris.org/lang/system#"
   
define count 5
while (> count 0) 
   print
       'spawn-example: %s %s'
           .format count sys:argv 
   sleep 1000
   -- count