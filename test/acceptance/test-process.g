#
#
#
var OS-name 
    (System!getProperties).|os.name|
print OS-name

var proc
    cond
        (OS-name(.match "Windows.*"))
            System!spawn 'cmd.exe' '/c' 'echo' 'Testing Testing one two three'
        (OS-name(.match "Linux.*"))
            System!spawn "/bin/echo" 'Testing Testing one two three'
        (OS-name(.match "SunOS.*"))
            System!spawn "/bin/bash"  'echo' 'Testing Testing one two three'
        else
            raise 
                "Unknown operating system %s"(.format OS-name)
    
print proc

proc
   define reader (.getOutput)
   assertEqual 
       'Testing Testing one two three'
       reader (.getline)
       reader (.close)
   print (.waitFor)
   print 'exit value = ' 
      .exitValue
