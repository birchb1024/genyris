@prefix java 'http://www.genyris.org/lang/java#'

java:import |java.lang.Object|
java:import |java.lang.Class|
java:import |java.lang.Integer|
java:import |java.lang.String|


#for m in |java.lang.Integer|!vars
#    print m
var x
   |java.lang.Integer|!new-int 23
var y
   |java.lang.Integer|!|new-java.lang.String| '234'

var i 
    |java.lang.Integer|!|new-java.lang.String| '987'
print
   i (.getClass) 
   i (.hashCode) 
   i (.floatValue) 


var c 
  |java.lang.Class|
      .|forName-java.lang.String| 'java.lang.Class'
#for m in (c(.getMethods)) (print m)


var s 
   |java.lang.String|(.|new-java.lang.String| "Godel")

assert
   equal? ^('Go' 'el')
       s(.|split-java.lang.String| 'd')

var d 
   |java.lang.String|(.|new-java.lang.String| "Godel")

var Dummy 
  java:import |org.genyris.test.java.JavaDummy|   
assert 
   equal? ^('0' '1' '2') 
       Dummy(.staticMethod1-int 3)
assert 
   equal? ^(0 1 2) 
       Dummy(.staticMethod2-int 3)
var d (Dummy(.new))
assert 
   equal? 42 
      d(.getInt)
d(.setInt-int 37)
assert 
   equal? 37 
      d(.getInt)

assert 
   equal? 'org.genyris.test.java.JavaDummy' 
       (d(.getClass))(.getName)

assert 
   equal? 
        d.vars
        ^(staticField intField longField charField floatField doubleField booleanField byteField shortField stringField privateField self vars classes) 
d (.intField = 99)
assert (equal? d!intField 99)
d (.shortField = 12)
assert (equal? d!shortField 12)
d (.byteField = 123)
assert (equal? d!byteField 123)
d(.floatField = 0.5)
assert (equal? d!floatField 0.5)
d(.doubleField = 2.25)
assert (equal? d!doubleField 2.25)
d(.longField = 2424234)
assert (equal? d!longField 2424234)
d(.charField = 'A')
assert (equal? d!charField 'A')
d(.stringField = 'WOw!')
assert (equal? d!stringField 'WOw!')
d(.booleanField = true)
assert (equal? d!booleanField true)
d(.booleanField = nil)
assert (equal? d!booleanField nil)

d (.privateField = 99)
assert (equal? d!privateField 99)

d (.staticField = 1000)
assert (equal? d!staticField 1000)

assert 
   equal? 
      d(.|method1-[Ljava.lang.String;| ^('a' 'b' 'c'))
      ^('a' 'b' 'c')


java:convert int 34
java:convert short 34
java:convert byte 34
java:convert long 34
java:convert float 34
java:convert double 34
java:convert boolean nil
java:convert char 'X'
java:convert string 'XYZ'
java:convert ^|[Ljava.lang.String;| ^('XYZ' 'ABC')

var x
 java:convert ^|[Ljava.lang.String;| ^('XYZ' 'ABC')

assert (equal? ^('XYZ' 'ABC') (java:toGenyris x))

var y
 java:convert ^|[Ljava.lang.Integer;| ^(1 2 3)

assert (equal? ^(1 2 3) (java:toGenyris y))

var z
 java:convert ^|[Ljava.lang.Object;| ^(1 2 3)
print (java:toGenyris z)


#
#
#
def run() 
   include 'testscripts/test-java.g'

