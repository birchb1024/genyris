#
# Find all source files
#
@prefix u "http://www.genyris.org/lang/utilities#"

def printIt (dir file)
   define path ("%a/%a" (.format dir file))
   cond
      (file (.match '(.*)\\.g'))
            print path

def findAllFiles (top regexp func)
   define f (File(.new top))
   define files (f(.list))
   while files
      define f (left files)
      define path ('%a/%a' (.format top f))
      func top f
      cond
         (File!static-is-dir? path)
              findAllFiles path regexp func
      files = (right files)

findAllFiles '.' '' printIt

