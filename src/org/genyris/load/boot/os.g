#
# Operating System module additions.
#
os
  define .name ((.getProperties).|os.name|)
  define .tempdir
    cond
       (.name(.match "Windows.*"))
          'C:\\temp'
       else
          '/tmp'