#
# Tools for Tracing and Debug
#
defmacro trace (stuff)
   #
   # Trace an expression by printing it and its evaluation
   #
   define trace-result (gensym 'trace-result')
   template
      do
         define $trace-result $stuff
         u:format "trace: %s => %s\n" ^$stuff $trace-result
         $trace-result    

