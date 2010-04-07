@prefix java 'http://www.genyris.org/lang/java#'
@prefix u "http://www.genyris.org/lang/utilities#"

java:import 'java.lang.Object' as Jobject
java:import 'java.lang.Class'
java:import 'java.lang.Integer' as Integer
java:import 'java.lang.String' as JavaString
java:import 'java.io.File' as JFile

for m in Integer!vars
    print m
var x
   Integer!new-int 23
var y
   Integer!new-java_lang_String '234'

var i 
    Integer!new-java_lang_String '987'
print
   i (.getClass) 
   i (.hashCode) 
   i (.floatValue) 


var c 
  java_lang_Class
      .forName-java_lang_String 'java.lang.Class'
for m in (c(.getMethods)) (print m)


var s 
   JavaString(.new-java_lang_String "Godel")

assert
   equal? ^('Go' 'el')
       s(.split-java_lang_String 'd')

var d 
   JavaString(.new-java_lang_String "Godel")

java:import 'org.genyris.test.java.JavaDummy' as Dummy   
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
        ^(staticField intField longField charField floatField doubleField booleanField byteField shortField stringField privateField self vars classes java:class) 
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
      d(.method1-*Ljava_lang_String; ^('a' 'b' 'c'))
      ^('a' 'b' 'c')
print Dummy!vars
catch error 
   d(.failmethod1)
assert error
u:format "caught error = %s\n" error
catch error 
   d(.failmethod2)
assert error
u:format "caught error = %s\n" error


java:toJava int 34
java:toJava short 34
java:toJava byte 34
java:toJava long 34
java:toJava float 34
java:toJava double 34
java:toJava boolean nil
java:toJava char 'X'
java:toJava string 'XYZ'
java:toJava '[Ljava.lang.String;' ^('XYZ' 'ABC')

var x
 java:toJava '[Ljava.lang.String;' ^('XYZ' 'ABC')

assert (equal? ^('XYZ' 'ABC') (java:toGenyris x))

var y
 java:toJava '[Ljava.lang.Integer;' ^(1 2 3)

assert (equal? ^(1 2 3) (java:toGenyris y))

var z
 java:toJava '[Ljava.lang.Object;' ^(1 2 3)
print (java:toGenyris z)

var file ( JFile!new-java_lang_String '/')

java:import 'java.io.File' as FileJ

var file (FileJ(.new-java_lang_String '/'))
assert (equal? file!vars ^(separatorChar separator pathSeparatorChar pathSeparator self vars classes java:class))
assert (or (equal? file!separator '/') (equal? file!separator '\\'))
catch error
  file(.separator = ';')     
file(.compareTo-java_lang_Object file)
#
#
#
def run() 
   include 'testscripts/test-java.g'
#
# Add a method to a builtin
#   
java:import 'java.math.BigDecimal'
Bignum
    def .precision()
        (java:toJava 'java.math.BigDecimal' .self)
               .precision
assert (equal? (2134(.precision)) 4)

java:import 'java.lang.String'
String
    def .replace(old new)
        (java:toJava 'java.lang.String' .self)
               .replace-char-char old new

java:toJava 'java.lang.String' 'east'
java:import file!java:class
