#
# File IO Tests
#

catch err
   File!static-list-dir 'test/fixtures/test.pipe'
assert (equal? err 'File.static-list-dir: failed on test/fixtures/test.pipe')
assert
   equal?
      File!static-list-dir (prepend-home 'test/fixtures/folder')
      ^('file1.txt' 'file2.txt')

catch err
   File!static-open "qwerty" ^read
assert err

catch err
   File!static-open "/undefined/path/qwerty" ^write
assert err

assert (File!static-is-dir? '.')
assert
   not
      File!static-is-dir? (prepend-home 'test/fixtures/test.pipe')
   
cond
    (os!name(.match "Windows.*"))
        assert (equal? 'C:\\' (File!static-abs-path '/'))
    else
        assert (equal? '/' (File!static-abs-path '/'))
   
define fixture 
  "%a/foo.txt"(.format os!tempdir)
define fi
   File(.new fixture)
define out
   fi (.open ^write)
out (.format "Hello\n")
out (.format "World\n")
out
  .format "  A: %a%n  S: %s%n  C: ~c ~~\n"  "GONZO\n"  "ALONZO\n"  
out (.close)

define fi
   File(.new fixture)
define in
   fi (.open ^read)
print (in(.read))

in(.close)

def dump()
  var fi (File(.new fixture))
  var in (fi (.open ^read))
  var ch nil
  var count 0
  while (in(.hasData))
     setq count (+ count 1)
     write (in(.read)) ^-
  in(.close)
  print "\n" count

catch errr ('not a parser' (ParenParser!read))
assert (equal? errr 'Non-Parser passed to a Parser method.')
var pars (ParenParser(.new "foo"))
assert (equal? pars pars)   
def parse-string()
  var script "343 (cons 1 2) (list 1 2 3 4 5)"
  var parser (ParenParser(.new script))
  var exp nil
  while
     not
        equal? EOF
           setq exp (parser(.read))
     write exp
     display "\n"
  parser(.close)

def parse-file()
  var fi (File(.new (prepend-home "test/fixtures/factorial.lsp")))
  var in (fi (.open ^read))
  var parser (ParenParser(.new in))
  var exp nil
  while
     not
        equal? EOF
           setq exp (parser(.read))
     write exp
     display "\n"
  parser(.close)include 'test/acceptance/test-file.g'


def parse-sfs-file()
  var fi (File(.new (prepend-home "test/fixtures/test.sfs")))
  var in (fi (.open ^read))
  var sfs (StringFormatStream(.new in))
  var parser (ParenParser(.new sfs))
  var exp nil
  while
     not
        equal? EOF
           setq exp (parser(.read))
     write exp
     display "\n"
  parser(.close)


def eval-sfs-file()
  var fi (File(.new (prepend-home "test/fixtures/test.sfs")))
  var in (fi (.open ^read))
  var sfs (StringFormatStream(.new in))
  var parser (ParenParser(.new sfs))
  var exp nil
  while
     not
        equal? EOF
           setq exp (eval (parser(.read)))
     display exp
  parser(.close)

######
define CSVFILE (prepend-home "test/fixtures/test.csv")
define fi
   File(.new CSVFILE)
define in
   fi (.open ^read)
assert
    and
        equal? (in(.getline)) "#subject,object,predicate"
        equal? (in(.getline)) "Joe,type,person"
        equal? (in(.getline)) 'Joe,owns,"Ford, Falcon"'
        equal? (in(.getline)) "Joe,worksAt,office"
in (.close)
define in
   fi (.open ^read)
define $line ""
while (not (equal? EOF (setq $line (in(.getline)))))
    define aslist ($line(.split ","))
    define tr (triple (parse (car aslist)) (parse (cadr aslist)) (parse (cadr (cdr aslist))))
    print tr
in (.close)
dump
parse-file
parse-sfs-file
eval-sfs-file
