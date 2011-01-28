@prefix java 'http://www.genyris.org/lang/java#'

java:import 'com.ostermiller.util.Base64'

class Base64EncodedString(String)
    def .decode()
       com_ostermiller_util_Base64!decode-java_lang_String .self 

String
   def .toBase64()
      tag Base64EncodedString
         com_ostermiller_util_Base64!encode-java_lang_String .self
   def .fromBase64()
      Base64EncodedString!decode 


def .encodeIntegers(listOfInts)
   com_ostermiller_util_Base64!encodeToString-*B
        java:toJava '[B' listOfInts
                   