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
   def .fromBase64asUnsignedIntegers()
      loop-left
         com_ostermiller_util_Base64!decodeToBytes-java_lang_String .self
         function (x)
            cond
               (< x 0)
                   + 256 x
               else
                   x


def .encodeUnsignedIntegers(listOfInts)
   com_ostermiller_util_Base64!encodeToString-*B
        java:toJava '[B' 
              loop-left listOfInts
                  function(x)
                     cond
                        (> x 256)
                            raise "number > 256"
                        (> x 127) 
                            - x 256
                        else
                           x
           
                   