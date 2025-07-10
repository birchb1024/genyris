@ns sys "http://www.genyris.org/lang/system#"
@ns java 'http://www.genyris.org/lang/java#'

java:import 'java.lang.Package'
define package
    Package!getPackage-java_lang_String 'org.genyris.interp'

define .version
    package(.getImplementationVersion)

define .title
    package(.getSpecificationTitle)

define .specification
    package(.getSpecificationVersion)
