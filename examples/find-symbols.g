#
# function to find symbols matching a pattern
#

def find(pattern)
   def like? (symbol)
      and
         bound? symbol
         (asString symbol)
             .match 
                '.*%a.*'
                   .format pattern
   for sym in (symlist)
      cond
         (like? sym)
             stdout
                .format '%s %a\n' sym (symbol-value sym)
find 'find'
      
