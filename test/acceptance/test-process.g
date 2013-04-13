#
#  Test OS Process Spawning, and reading from its output.
#
@prefix java 'http://www.genyris.org/lang/java#'

print Process!vars
var OS-name 
    (os!getProperties).|os.name|

var proc
    cond
        (OS-name(.match "Windows.*"))
            os!spawn 'cmd.exe' '/c' 'echo' 'Testing' 'Testing' 'one' 'two' 'three'
        (OS-name(.match "Linux.*"))
            os!spawn "/bin/echo" 'Testing Testing one two three'
        (OS-name(.match "SunOS.*"))
            os!spawn "/bin/bash"  'echo' 'Testing Testing one two three'
        (OS-name(.match "Mac OS X.*"))
            os!spawn "/bin/bash"  "-c" 'echo Testing Testing one two three'
        else
            raise 
                "Unknown operating system %s"(.format OS-name)

print (proc.vars) (proc.classes)
proc
   define reader (.getOutput)
   assertEqual 'Testing Testing one two three'
       reader (.getline)
   reader (.close)
   assertEqual 0 (.waitFor)
   assertEqual 0 (.exitValue)
