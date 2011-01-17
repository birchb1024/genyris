#
#
#

var proc
      System!spawn 'cmd.exe' '/c' 'echo' 'Testing Testing one two three'

print proc

proc
   define reader (.getOutput)
   reader (.copy stdout)
   stdout
      .flush
   print (.waitFor)
   print 'exit value = ' 
      .exitValue
