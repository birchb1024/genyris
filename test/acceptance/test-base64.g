#
#
#
import Base64

define leviathan 'Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.'
define leviathanEncoded    'TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=' 

assertEqual
    leviathan
    leviathanEncoded(.fromBase64)
    
assertEqual
    leviathan(.toBase64)
    leviathanEncoded

def check (clear encoded)
  assertEqual
    clear(.toBase64)
    encoded
    
check 'carnal pleasure.' 'Y2FybmFsIHBsZWFzdXJlLg=='
check 'carnal pleasure' 'Y2FybmFsIHBsZWFzdXJl'
check 'carnal pleasur' 'Y2FybmFsIHBsZWFzdXI='
check 'carnal pleasu' 'Y2FybmFsIHBsZWFzdQ=='

assertEqual
   Base64!encodeUnsignedIntegers (leviathan(.toInts 'ASCII'))
   leviathanEncoded

define numbers  ^(0 1 127 128 255 0)   
assertEqual
   numbers
   (Base64!encodeUnsignedIntegers numbers)
      .fromBase64asUnsignedIntegers

