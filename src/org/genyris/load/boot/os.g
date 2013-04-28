#
# Operating System module additions.
#
os
  define .name ((.getProperties).|os.name|)
  define .tempdir
    cond
       (.name(.match "Windows.*"))
          .getenv 'TEMP'
       else
          '/tmp'