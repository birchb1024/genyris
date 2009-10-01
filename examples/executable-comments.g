#./usr/bin/sh /opt/home/birchb/workspace/genyris/bin/genyris

df // (&rest body) 
    print body  # debug version 

def myfun()
   print
   // "in line comment"
   +
      // + 1      # this causes an error because // returns nil.
          + 34
          + 45
      99
      
// def allcommentedout(arg)
  lambda (x)
    cond
      (eq? nil (cdr x))
         car x
      else
         last (cdr x)
   