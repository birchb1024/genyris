#
# print out the class hierarchy
#
@prefix u "http://www.genyris.org/lang/utilities#"

def indent(depth)
    while (> depth 0)
       u:format '   '
       depth = (- depth 1)

def printAllClasses(root depth)
    indent depth
    u:format ' %a\n' root!classname
    for c in root!subclasses
       printAllClasses c (+ 1 depth)

printAllClasses Thing 0

