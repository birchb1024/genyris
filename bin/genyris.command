#!/bin/sh
#
export GENYRIS_HOME="`dirname $0`/.."
java -Xmx256M -jar ${GENYRIS_HOME}/dist/genyris-*.jar $*

# Alternate form:
# java -Xmx256M -classpath ${GENYRIS_HOME}/dist/\* org.genyris.interp.ClassicReadEvalPrintLoop $*
