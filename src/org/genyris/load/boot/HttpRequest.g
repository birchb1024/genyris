## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
 
class HttpRequest()
    def .getMethod() (nth 0 .self)
    def .getPath() (nth 1 .self)
    def .getHeaders() (nth 2 .self)
    def .getParameters() (nth 3 .self)
    def .getClient() (nth 4 .self)
    def .getClientIP() (left (nth 4 .self))
    def .getClientHostname() (right (nth 4 .self))
    def .getClientPort() (nth 5 .self)
    def .getSessionID() (nth 6 .self)
    def .getAuthorizationHeader()
       (.getHeaders)
            .lookup 'authorization'    
    def .getVirtualHost()
       (.getHeaders)
            .lookup 'host'    
    def .getBasicUsernamePassword()
      # Returns a dictionary with username and password strings.
      var headers (.getHeaders)
      cond 
         (not (headers (.hasKey 'authorization')))
             dict (.username = '')(.password = '')
         else
               var auth (headers (.lookup 'authorization'))
               var splits (auth(.split))
               var decoded 
                  (nth 1 splits )(.fromBase64)
               var up (decoded(.split ':'))
               var result (dict)
               cond
                   (equal? (length up) 2)
                       dict
                           .username = (nth 0 up)
                           .password = (nth 1 up)
                   (equal? (length up) 1)
                       dict
                           .username = (nth 0 up)
                           .password = ''
                   else
                        dict (.username = '')(.password = '')
    def .toHTML ()
       template
          div()
             table((width = "100%"))
               tr()
                 td() "Method: "
                 td() "Path: " 
                 td() "Client IP: " 
                 td() "Client Name: "
                 td() "Client Port: " 
                 td() "SessionID: "
               tr()
                 td() $(.getMethod)
                 td() $(.getPath)          
                 td() $(left (.getClient))
                 td() $(right (.getClient))
                 td() $(.getClientPort)
                 td() $(.getSessionID)
             div() "Headers:" $((.getHeaders)(.toHTML))
             div() "Parameters:" $((.getParameters)(.toHTML))
