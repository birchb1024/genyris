@prefix u "http://www.genyris.org/lang/utilities#"
def u:format(&rest args)
   stdout
      apply .format args

defmacro u:debug(thing)
  template
    u:format "%a => %a%n" ^,thing ,thing

def u:getLocalTime()
   format-date ((System.ticks)) "dd MMM yyyy HH:mm:ss"

def u:printSymbolTable()
  (symlist)
    .each (x)
      (stdout (.format "%a%n" x))
