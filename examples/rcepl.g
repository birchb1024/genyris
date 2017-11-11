@prefix u "http://www.genyris.org/lang/utilities#"
@prefix ver "http://www.genyris.org/lang/version#"
@prefix sys "http://www.genyris.org/lang/system#"
@prefix java 'http://www.genyris.org/lang/java#'
@prefix : "http://www.genyris.org/lang/types#"
@prefix foaf 'http://xmlns.com/foaf/0.1/'

include "lib/types.g"
include "lib/classify.g"

def classify-from-list(obj list-of-classes)
   for klass in 

def read-classify-eval-print-loop(root-classes)

   define looping true
   define parser (IndentedParser(.new stdin true))
   parser
      .prefix ^sys "http://www.genyris.org/lang/system#"
      .prefix ^u "http://www.genyris.org/lang/utilities#"
      .prefix ^java 'http://www.genyris.org/lang/java#'
      .prefix ^foaf 'http://xmlns.com/foaf/0.1/'
   while looping
       u:format '\n> '
       define bt nil
       define result nil
       catch (errors bt)
            define expression (parser (.read))
            cond
                (equal? expression EOF)
                    print 'Bye!'
                    setq looping nil
                else
                    map-left root-classes
                        lambda (klass)
                           classify klass (left expression)
                    #sys:print-classnames expression
                    setq result (eval expression)
                    map-left root-classes
                        lambda (klass)
                           classify klass result
                    u:format '%s # ' result
                    sys:print-classnames result
       cond
            errors
               u:format "*** Error - %s\n" errors
               sys:printBackTrace bt
               setq bt nil

Bignum
  def .new(obj)
     define parser (ParenParser(.new obj))
     parser(.read)

class DemoString(String)
  def .valid? (obj)
      is-instance? obj String

class DemoURL(DemoString)
  def .valid? (obj)
      obj(.regex 'http://[^/]+/.*')

def >< (min max x)
    # Number x value between min and max.
    and
        > x min
        < x max
       
class DemoDate(DemoString)
  define separator '/'
  def .valid? (obj)
      and
        obj(.regex '[0-9][0-9]/[0-9][0-9]/[0-9][0-9][0-9][0-9]')
        > (obj(DemoDate!year)) 0
        >< 0 13 (obj(DemoDate!month))
        >< 0 32 (obj(DemoDate!day))
       
  def partAsNumber(this index)
        Bignum!new
            nth index (this(.split separator))
  def .year()
        partAsNumber .self 2
  def .month()
        partAsNumber .self 1
  def .day()
        partAsNumber .self 0


def classifyNreturn (klass obj)
   classify klass obj
   the obj

assert (not (DemoURL!valid? "32"))
assert (DemoURL!valid? 'http://www.genyris.org/lang/java#')

assert
    is-instance? (classifyNreturn DemoString 'foo') DemoString
assert
    is-instance? (classifyNreturn DemoString 'http://www.genyris.org/lang/java#') DemoURL

read-classify-eval-print-loop (list :Record :SequenceOfRecords DemoString)

