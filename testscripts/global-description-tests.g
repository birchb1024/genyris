#
#
#
@prefix u "http://www.genyris.org/lang/utilities#"


def getSubClassesOf(super)
    (types.Descriptions (.select nil ^subClassOf super))(.asTriples)
    
def dump(list)
    while list
        u:format "%a %a %a%n" 
            (car list)(.subject)
            (car list)(.predicate)
            (car list)(.object)
        list = (cdr list)

dump (getSubClassesOf ^Builtin)