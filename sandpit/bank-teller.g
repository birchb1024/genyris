@ns u "http://www.genyris.org/lang/utilities#"
include '../../alex-today/shell.g'
var account-number 987878676

u:format '%a:%a ERROR Insufficient funds in account %a\n' @FILE @LINE account-number 

^(ERROR)(.log 'Insufficient funds in account' account-number)
 
