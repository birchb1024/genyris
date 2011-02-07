#
# Find all files and execute a function
#
# Usage: do (include 'lib/find.g') (walk-directory-tree '.' print)
def walk-directory-tree (top func)
   # top - root directory path
   # func - a closure of the form (function (pathname) ...) - pathname is the path of the file.
   define f (File(.new top))
   define files (f(.list))
   for f in files
      define path ('%a/%a' (.format top f))
      func path
      cond
         (File!static-is-dir? path)
              walk-directory-tree path func

