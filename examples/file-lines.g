@prefix sys "http://www.genyris.org/lang/system#"
include 'lib/find.g'

def count-strings-in-file(regex path)
   define todo-count 0
   define fd 
      (File(.new path))
         .open ^read
   for line in fd
      cond
         (line (.match regex))
            setq todo-count (+ 1 todo-count)
   todo-count

print sys:argv
define file-regex (nth 1 sys:argv)
define tag-regex (nth 2 sys:argv)
define path (nth 3 sys:argv)
#stdout
#   .format "Number of %s in %s: %a" regex path (count-strings-in-file regex path)
define total-count 0
walk-directory-tree path
   function (path)
      cond
         ((path(.toLowerCase))(.match (file-regex(.toLowerCase))))
            define count (count-strings-in-file tag-regex path)
            cond
               (> count 0)
                  setq total-count (+ total-count count)
                  stdout
                     .format "%s: %a\n" path count
stdout
   .format "Total Number of %s : %a\n" tag-regex total-count

