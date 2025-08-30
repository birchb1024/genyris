@ns u "http://www.genyris.org/lang/utilities#"

def log ((level = PairSource) &rest args)
    u:format '%a:%a %a ' level!filename level!line-number level
    apply u:format args
    u:format '\n'

var account-number 987878676

u:format '%a:%a ERROR Insufficient funds in account %a\n' @FILE @LINE account-number 

log ^(ERROR) 'Account %s, Insufficient funds' account-number
 
