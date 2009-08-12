:: start Sub Lamp server
start "Notification Server" /MIN java -jar -cp . genyris-bin*.jar -eval do (web.serve 8111 \"C:/workspace/genyris/examples/notification-server.lin\") (read)