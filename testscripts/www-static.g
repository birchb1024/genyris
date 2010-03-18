@prefix sys "http://www.genyris.org/lang/system#"
df httpd-serve (request)
   print sys:argv
   list ^SERVE-FILE (nth 1 sys:argv) (request(.getPath)) ^ls

