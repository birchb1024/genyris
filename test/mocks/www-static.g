@prefix sys "http://www.genyris.org/lang/system#"
df httpd-serve (request)
   list ^SERVE-FILE (nth 1 sys:argv) (request(.getPath)) ^ls

