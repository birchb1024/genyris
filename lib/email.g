@prefix java 'http://www.genyris.org/lang/java#'

java:import 'java.util.Properties'

java:import 'javax.mail.Message'
java:import 'javax.mail.Message$RecipientType'
java:import 'javax.mail.MessagingException'
java:import 'javax.mail.Session'
java:import 'javax.mail.Transport'
java:import 'javax.mail.Address'
java:import 'javax.mail.internet.InternetAddress'
java:import 'javax.mail.internet.MimeMessage'

MimeMessage
  def .setRecipients(to-list)
    for recipient in to-list
        .addRecipient-javax_mail_Message$RecipientType-javax_mail_Address Message$RecipientType!TO =
            (InternetAddress(.new-java_lang_String recipient))

def .postMail(recipients subject message from server)
    define props (Properties(.new))
    props
       .put-java_lang_Object-java_lang_Object
            java:toJava 'java.lang.String' "mail.smtp.host"
            java:toJava 'java.lang.String' server
    #
    # create some properties and get the default Session
    define session
        Session(.getDefaultInstance-java_util_Properties props)
    #
    # create a message
    define msg 
        MimeMessage(.new-javax_mail_Session session)
    # set the from and to address
    msg
       .setFrom-javax_mail_Address
           InternetAddress(.new-java_lang_String from)
    msg(.setRecipients recipients)
    #
    # Setting the Subject and Content Type
    msg
       .setSubject-java_lang_String subject
       .setContent-java_lang_Object-java_lang_String (java:toJava 'java.lang.String' message) "text/plain"
    #
    Transport(.send-javax_mail_Message msg)
    
          