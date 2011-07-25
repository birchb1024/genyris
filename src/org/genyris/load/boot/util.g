@prefix u "http://www.genyris.org/lang/utilities#"
@prefix date "http://www.genyris.org/lang/date#"

def u:format(&rest args)
   stdout
      apply .format args

defmacro u:debug(thing)
  template
    u:format "%a => %a%n" ^,thing ,thing

def u:getLocalTime()
   tag ShortDateTimeString
      date:format-date ((os.ticks)) "dd MMM yyyy HH:mm:ss"

def u:printSymbolTable()
  (symlist)
    .each (x)
      (stdout (.format "%a%n" x))
