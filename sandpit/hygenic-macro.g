@prefix u   "http://www.genyris.org/lang/utilities#"

defmacro my-unless (condition &rest body)
  template
    cond
        (not $condition)
            $@body

my-unless true
     u:format "This should not be printed!\n"
     
my-unless nil
     u:format "This should be printed!\n"
     
do
    def not(x) x
    my-unless true
         u:format "This is not Hygenic..\n"
    
