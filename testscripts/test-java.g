
def run() 
   include 'testscripts/test-java.g'
Java!import 'java.lang.Integer'
for m in |java.lang.Integer|!vars
    print m
print
   |java.lang.Integer|!new-int 23
print
   |java.lang.Integer|!|new-java.lang.String| '234'

var i 
    |java.lang.Integer|!|new-java.lang.String| '987'
print i
print
   i (.getClass) 
   i (.hashCode) 
   i (.floatValue) 
   