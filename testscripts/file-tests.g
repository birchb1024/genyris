define fixture "foo.txt"
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
     count = (+ count 1)
     write (in(.read)) ^-
  in(.close)
  print "\n" count


def parse-string()
  var script "343 (cons 1 2) (list 1 2 3 4 5)"
  var parser (ParenParser(.new script))
  var exp nil
  while
     not
        equal? EOF
           exp = (parser(.read))
     write exp
     display "\n"
  parser(.close)

def parse-file()
  var fi (File(.new "testscripts/factorial.lsp"))
  var in (fi (.open ^read))
  var parser (ParenParser(.new in))
  var exp nil
  while
     not
        equal? EOF
           exp = (parser(.read))
     write exp
     display "\n"
  parser(.close)


def parse-sfs-file()
  var fi (File(.new "testscripts/test.sfs"))
  var in (fi (.open ^read))
  var sfs (StringFormatStream(.new in))
  var parser (ParenParser(.new sfs))
  var exp nil
  while
     not
        equal? EOF
           exp = (parser(.read))
     write exp
     display "\n"
  parser(.close)


def eval-sfs-file()
  var fi (File(.new "testscripts/test.sfs"))
  var in (fi (.open ^read))
  var sfs (StringFormatStream(.new in))
  var parser (ParenParser(.new sfs))
  var exp nil
  while
     not
        equal? EOF
           exp = (eval (parser(.read)))
     display exp
  parser(.close)

######
define CSVFILE "testscripts/fixtures/test.csv"
define fi
   File(.new CSVFILE)
define in
   fi (.open ^read)
assert
    and
        equal? (in(.getline)) "#subject,object,predicate"
        equal? (in(.getline)) "Joe,type,person"
        equal? (in(.getline)) "Joe,owns,Ford"
        equal? (in(.getline)) "Joe,worksAt,office"
in (.close)
define in
   fi (.open ^read)
define $line ""
while (not (equal? EOF ($line = (in(.getline)))))
    define aslist ($line(.split ","))
    define tr (triple (parse (car aslist)) (parse (cadr aslist)) (parse (cadr (cdr aslist))))
    print tr
in (.close)
dump
parse-file
parse-sfs-file
eval-sfs-file