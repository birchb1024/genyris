@prefix java 'http://www.genyris.org/lang/java#'
@prefix u "http://www.genyris.org/lang/utilities#"
#
#  Example shows use of Java FFI calls
#
java:import 'java.io.File' as FileJ

def ls(path)
   #
   # recursive directory listing
   #
   def indent(depth)
      while (> depth 0)
          u:format '   '
          depth = (- depth 1)
   def ls-aux(depth path)
       var f 
            FileJ!new-java_lang_String path
       for filename in (f(.list))
          var pathname ("%a/%a" (.format path filename))
          var file (FileJ!new-java_lang_String pathname)
          indent depth
          cond
              (file(.isDirectory))
                 u:format '%a/\n' filename
                 ls-aux (+ 1 depth) pathname
              else
                 u:format '%a %a\n' filename (file(.length))               
   ls-aux 0 path

ls '.'