@prefix java 'http://www.genyris.org/lang/java#'

java:import 'java.lang.Process' as Process
java:import 'java.io.InputStreamReader' as InputStreamReader

Process
   def .getOutput()
         java:toGenyris (InputStreamReader!new-java_io_InputStream (.getInputStream))
   def .getError()
         java:toGenyris (InputStreamReader!new-java_io_InputStream (.getErrorStream))

os
   # monkey-patch os spawn to return a Process object
   var builtin-spawn (the os!spawn)
   def .spawn (&rest args)
      tag Process
          apply builtin-spawn args
