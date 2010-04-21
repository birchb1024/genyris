@prefix java 'http://www.genyris.org/lang/java#'

java:import 'au.com.bytecode.opencsv.CSVReader' as CSVReader
java:import 'java.util.ArrayList' as ArrayList

def .read(file-descriptor field-separator-char quote-char)
    var reader 
       CSVReader(.new-java_io_Reader-char-char file-descriptor field-separator-char quote-char)
    (reader(.readAll))(.toArray)
