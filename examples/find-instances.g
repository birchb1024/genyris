#
# Find objects at the root with a particular class
#
@prefix u 'http://www.genyris.org/lang/utilities#'

def find-instances(klass)
  print klass
  define sl (symlist) # cache it
  for s in sl
    cond 
      (bound? s) # is the symbol bound
        use (eval s) 
          cond
            (equal? klass (left .classes)) # is it the class we want?
              u:format "%a\n" s
  print ''            
#
# Example
find-instances LazyProcedure
find-instances EagerProcedure