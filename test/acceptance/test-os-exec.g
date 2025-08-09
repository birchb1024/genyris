
assertEqual ^(('d41d8cd98f00b204e9800998ecf8427e  /dev/null'))
    os!exec 'md5sum' '/dev/null'

assertEqual ^(('d41d8cd98f00b204e9800998ecf8427e  /dev/null'))
    os!exec ^('md5sum' '/dev/null')

assertEqual ^((''))
    os!exec '/bin/bash' '-c' 'echo $WIGGLES'

assertEqual ^((''))
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES')

assertEqual ^(('nil'))
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES') ^(('WIGGLES'))

assertEqual ^(("BIG RED CAR"))
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES') ^(('WIGGLES' = "BIG RED CAR"))

assertEqual ^(("23"))
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES') ^(('WIGGLES' = 23))

assertEqual ^(('(1 2)'))
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES') ^(('WIGGLES' = (1 2)))

assertEqual ^(('12'))
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES') (list (cons ^'WIGGLES' (* 3 4)))

assertEqual ^(('(graph)'))
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES') (list (cons ^'WIGGLES' (graph)))

assertEqual ^(('123'))
  os!exec ^('bash' '-c' 'echo $ZZZZZZZ') ^(('ZZZZZZZ' = 123))

assertEqual ^(('123'))
  os!exec ^('bash' '-c' 'echo $USER') ^(('USER' = 123))

var ENV (tag Alist (os!getenv))
setq ENV (cons (cons 'AVALU' 768213478213 ) ENV)

assertEqual ^(('768213478213'))
  os!exec ^('bash' '-c' 'echo $AVALU') ENV

assertEqual ^(('222'))
  os!exec ^('bash' '-c' 'echo $qwe') ^(('qwe' = 111)('qwe' = 222))

defmacro assertError(&rest code)
    template
      do
        catch err
          $@code
        cond
          err
            print (list "Good error" ^$code err)
          else
            raise (list "did not fail when expected" ^$code)

assertError os!exec nil

assertError os!exec 666
 
assertError os!exec '/dev/null'
 
assertError os!exec ^((fubar))
 
assertError os!exec ^('/bin/bash' (fubar))
 
assertError os!exec ^('/bin/bash' = fubar)
 
assertError os!exec ^('md5sum' '/dev/null') ^(nil)
 
assertError os!exec ^('md5sum' '/dev/null') ^(12)
 
assertError os!exec ^('/bin/bash' '-c' 'env |sort') ^((12))

assertError os!exec ^('/bin/bash' '-c' 'env |sort') ^((nil))

assertError os!exec ^('/bin/bash' '-c' 'env |sort') ^((nil = 12))
