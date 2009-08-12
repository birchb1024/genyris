:: start Sub Lamp server
start "Notification Server" /MIN java -jar -cp . genyris-bin*.jar -eval do (web.serve 8080 \"C:/workspace/notifications/notification-server.lin\") (read)