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

assert (File!is-dir? '.')
assert
   not
      File!is-dir? (prepend-home 'test/fixtures/test.pipe')
   
cond
    (os!name(.match "Windows.*"))
        assert (equal? 'C:\\' (File!static-abs-path '/'))
    else
        assert (equal? '/' (File!static-abs-path '/'))

def scan-file(filename)
  # Read a file and scan the contents
  # return (number-of-characters checksum)
  var fi (File!new filename)
  var in (fi (.open ^read))
  var count 0
  var checksum 0
  while (in(.hasData))
     setq count (+ count 1)
     define chint (in(.read))
     setq checksum (+ checksum chint)
  in(.close)
  list count checksum

#
# Create a file and put data into it
#   
define fixture 
  "%a/test-file-%a-%a.txt"(.format os!tempdir ((os!getProperties).|user.name|) (os!ticks))
  # TODO Small but finite chance this may fail if run multiple times at once.
define fi
   File!new fixture
define out
   fi (.open ^write)
out (.format "Hello\n")
out (.format "World\n")
out
  .format "  A: %a%n  S: %s%n  C: ~c ~~\n"  "GONZO\n"  "ALONZO\n"  
out (.close)

assertEqual (scan-file fixture) ^(51 3400)

#
# Parser Tests
#

catch errr ('not a parser' (ParenParser!read))
assert (equal? errr 'Non-Parser passed to a Parser method.')
var pars (ParenParser!new "foo")
assert (equal? pars pars)   

def parse-string(script)
  # Parse a string - return result as a list of expressions
  var result nil
  var parser (ParenParser!new script)
  var exp nil
  while
    not
        equal? EOF
           setq exp (parser(.read))
    setq result (append result (list exp))
  parser(.close)
  result

assertEqual
  parse-string "343 (cons 1 2) (list 1 2 3 4 5)"
  quote
    343 (cons 1 2)
      list 1 2 3 4 5



def parse-file(file-path)
  # Parse a file - return result as a list of expressions
  var result nil
  var fi (File!new file-path)
  var in (fi (.open ^read))
  var parser (ParenParser(.new in))
  var exp nil
  while
      not
        equal? EOF
          setq exp (parser(.read))
      setq result (append result (list exp))
  parser(.close)
  result

define factorial-content
  quote
     (|http://www.genyris.org/lang/utilities#format| "Factorial") 
       assertEqual (quote 2) (quote 2)
       assertEqual 'w' "w"
       assertEqual (quote (1 = 2))
          cons 1 2
       def factorial
          n
          if (< n 2) 1
             * n
                factorial (- n 1)
       and (equal? (factorial 0) 1)
          equal? (factorial 1) 1
          equal? (factorial 2) 2
          equal? (factorial 3) 6
          equal? (factorial 4) 24
          equal? (factorial 5) 120
          equal? (factorial 6) 720
          equal? (factorial 7) 5040
          equal? (factorial 8) 40320
          equal? (factorial 9) 362880
          equal? (factorial 10) 3628800
          equal? (factorial 15) 1307674368000
          equal? (factorial 20) 2432902008176640000
          equal? (factorial 25) 15511210043330985984000000

assertEqual factorial-content
  parse-file (prepend-home "test/fixtures/factorial.lsp")



#
# Test CSV file reading
#
define CSVFILE (prepend-home "test/fixtures/test.csv")
define fi (File!new CSVFILE)
define in
   fi (.open ^read)
assert
    and
        equal? (in(.getline)) "#subject,object,predicate"
        equal? (in(.getline)) "Joe,type,person"
        equal? (in(.getline)) 'Joe,owns,"Ford, Falcon"'
        equal? (in(.getline)) "Joe,worksAt,office"
in (.close)

# Convert to triples 
define in
   fi (.open ^read)
define dline ""
while (not (equal? EOF (setq dline (in(.getline)))))
    define aslist (dline(.split ","))
    define tr (triple (parse (car aslist)) (parse (cadr aslist)) (parse (cadr (cdr aslist))))
in (.close)

#
# Test SFS File
#
define sfs-fixture (prepend-home "test/fixtures/test.sfs")
def parse-sfs-file()
  # Parse the test file and return contents as a list of expressions.
  var result nil
  var fi (File!new sfs-fixture)
  var in (fi (.open ^read))
  var sfs (StringFormatStream(.new in))
  var parser (ParenParser(.new sfs))
  var exp nil
  while
     not
        equal? EOF
           setq exp (parser(.read))
     setq result (append result (list exp))
  parser(.close)
  result


def eval-sfs-file()
  # Parse and evaluate the test file and return results as a list of expressions.
  var result nil
  var fi (File!new sfs-fixture)
  var in (fi (.open ^read))
  var sfs (StringFormatStream(.new in))
  var parser (ParenParser!new sfs)
  var exp nil
  while
      not
        equal? EOF
          setq exp (eval (parser(.read)))
      setq result (append result (list exp))
  parser(.close)
  result

define sfs-content  
  quote
    "Hello World " (+ 1 2 3 4 5) " --\n" "A string" "\n<a href=\""
      + 4 5
      ~ "\">A link</a>\n"

assertEqual (parse-sfs-file) sfs-content

define sfs-content-evaled  
  quote
    "Hello World " 15 " --\n" "A string" "\n<a href=\"" 9 "\">A link</a>\n"

assertEqual (eval-sfs-file) sfs-content-evaled
