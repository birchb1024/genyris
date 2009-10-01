@prefix u "http://www.genyris.org/lang/utilities#"
@prefix sys "http://www.genyris.org/lang/system#"
@prefix email "http://www.genyris.org/lang/email#"

include "/workspace/genyris/examples/classify.g"

def usage()
    u:format "Usage: <scriptname> <client IP regular expression> <rules file> <mailserver> <reply-to email address>%n"
    u:format "You supplied %s\n" sys:argv 
    (System.exit) 1

if (equal? 5 (length sys:argv)) nil (usage)

define *client-id-regex* (nth 1 sys:argv) # client IP regular expression
define *rule-file* (nth 2 sys:argv)       # more classes for messages
define *mail-server* (nth 3 sys:argv)       
define *reply-to* (nth 4 sys:argv)       

var log ^()

class UsbLamp()
   var exePath "C:\\WINNT\\system32\\cmd.exe"
   var operations
      tag Alist
         quote
            ("allflashoff"    = "10 20 7 0")
               "alldark"      = "10 12 0 7"
               "allon"        = "10 12 7 0"
               "blueflash"    = "10 20 0 4"
               "blueflashoff" = "10 20 4 0"
               "blueoff"      = "10 12 0 4"
               "blueon"       = "10 12 4 0"
               "greenflash"   = "10 20 0 1"
               "greenflashoff"= "10 20 1 0"
               "greenoff"     = "10 12 0 1"
               "greenon"      = "10 12 1 0"
               "redflash"     = "10 20 0 2"
               "redflashoff"  = "10 20 2 0"
               "redoff"       = "10 12 0 2"
               "redon"        = "10 12 2 0"
               "alloff"       = "meta"

   def .isValidOperation?(code)
      member? code
        operations(.getKeys)

   def .getOperationCodeList()
      operations(.getKeys)

   def call-lamp(codes)
      catch ignore # the naughty dos program gives 1 status
          System
             apply .exec
                template
                    ,exePath "/c" "C:\\workspace\\notifications\\usblamp\\USBDOSAP.EXE " ,@(codes(.split))
      
   def change((op = String))
       var codes (operations (.lookup op))
       cond
         (equal? op "alloff")
             change "allflashoff"
             change "alldark"
         (null? codes)
             "no-op" #raise "UsbLamp sent bad op-code."
         else
             call-lamp codes
   def .changes(list-of-strings)
      cond
        (left list-of-strings)
            map-left list-of-strings change

class Noises()
    define mediaStore "c:\\WINNT\\Media\\"

    def .play(soundfile-name)
       cond
         (null? soundfile-name)
         (equal? "beep" soundfile-name)
               display "\a \a"
         else
            Sound(.play (mediaStore (.+ soundfile-name)))
            the soundfile-name

class Email()
    def .send(addresses message)
        u:format "sending %s %s %s %s %s" addresses message message *reply-to* *mail-server*
        email:send addresses message message *reply-to* *mail-server*

class StringMessage(String)
   def .valid?(str)
      is-instance? str String
   def .render-log-line() .self
   def .update-lamp()
   def .play-sounds()
   def .send-email()
   def .custom-action(context)
   def .log(context)
     (context.addToLog) .self .style-exp
   def .style(str)
      define .style-exp str
   .style "background: white;border 1px;"

class NullMessage(StringMessage)
  def .valid?(str)
     equal? str ""
  def .log(context)

class UsbMessage(StringMessage)
  def .valid?(str)
     (UsbLamp.isValidOperation?) str
  def .update-lamp()
    (UsbLamp.changes) (list .self)

class Message(StringMessage)

   def .valid?(str)
      #u:format ".valid? %s %s\n" .self str
      #print str
      cond
        ((UsbMessage.valid?) str) nil # Message and UsbMessage mutually exclusive
        else
           (function(pat) (str (.match pat))) .pattern-exp

   def .pattern (str)
      var .pattern-exp str

   def .lights (&rest light-codes)
      var .light-exp light-codes

   def .sounds (&rest sound-files)
      var .sound-exp sound-files

   def .email (list)
      var .email-recipients list

   def .update-lamp()
     (UsbLamp.changes) .light-exp

   def .play-sounds()
      cond
        .sound-exp
          (Noises.play) (left .sound-exp)

   def .send-email()
      cond
        .email-recipients
          (Email.send) .email-recipients .self
Message
   .pattern ".+"
   .lights nil
   .sounds "boing_1.wav"
   .email ^()

u:format "Loading %s%n" *rule-file*
include *rule-file*

class BuildNotification()
  # singleton
  var log nil
  var lastmessage nil
  define client "unknown"
  def .getLog() log
  def .reset-log()
      log = nil

  def addToLog(message style client)
    lastmessage = message
    u:format "%a %a %a %n" (u:getLocalTime) client message
    log = (cons (template (tr() (td() ,(u:getLocalTime)) (td((style = ,style)) ,message)(td(()) ,client))) log)

  def .addToLog(message style)
    addToLog message style client

  def .addToLogIfNew(message style)
    cond
      (not(equal? message lastmessage))
         addToLog message style

  def .alertUsers(operation nextclient)
    client = nextclient
    classify StringMessage operation
    #u:format "operation => %a%n" operation
    #u:format "operation types: %a%n" (operation.classes)
    operation
          .log BuildNotification
          .update-lamp
          .play-sounds
          .send-email
          .custom-action BuildNotification

def authorised?(req)
    (req(.getClientIP))
         .match (nth 1 sys:argv)

df httpd-serve (request)
   cond
     (authorised? request)
          process-request request
     else
        u:format "%a Unauthorised hit from %a%n" (u:getLocalTime) (request(.getClient))
        list 403 "text/html"
              template
                  html()
                     head()
                       title() "403 Unauthorised"
                       body()
                         h1() "403 Unauthorised"
        
def process-request (request)
   var params (request(.getParameters))
   var client (request(.getClient))
   var operation ""
   cond
     params
        operation = (params (.lookup "op"))
        BuildNotification(.alertUsers operation client)
   var result
    list 200 "text/html"
      template
          html()
             head()
               title() "Lamp Server"
             body()
                form()
                   input((name="op") (size="100") (value = ,operation)) ""
                   verbatim() "&nbsp;&nbsp;&nbsp;"
                   input((type="submit") (value="Go"))
                form()
                    input((type ="submit") (value ="Refresh"))
                a((href ="/?op=alloff")) "All Off  |"
                a((href ="/?op=greenon")) "  Green  |"
                a((href ="/?op=redon")) "  Red  |"
                a((href ="/?op=blueon")) "  Blue  "
                table()
                    ,(BuildNotification(.getLog))
   result


