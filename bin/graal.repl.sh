#!/bin/bash
#
set -euo pipefail
set -x
export GENYRIS_HOME="$(dirname $0)"

java -agentlib:native-image-agent=config-output-dir=/tmp/agent-output \
-cp "${GENYRIS_HOME}/dist/bsf.jar:${GENYRIS_HOME}/dist/checkstyle-5.3-all.jar:${GENYRIS_HOME}/dist/commons-io-2.4.jar:${GENYRIS_HOME}/dist/commons-logging-1.1.jar:${GENYRIS_HOME}/dist/dsn.jar:${GENYRIS_HOME}/dist/failureaccess-1.0.3.jar:${GENYRIS_HOME}/dist/genyris-bin-1.0.1-30-g0735ab0-1.jar:${GENYRIS_HOME}/dist/guava-33.4.8-jre.jar:${GENYRIS_HOME}/dist/hamcrest-core-1.3.jar:${GENYRIS_HOME}/dist/httpclient-4.5.1.jar:${GENYRIS_HOME}/dist/httpcore-4.4.3.jar:${GENYRIS_HOME}/dist/imap.jar:${GENYRIS_HOME}/dist/jline-2.11.jar:${GENYRIS_HOME}/dist/junit.jar:${GENYRIS_HOME}/dist/mailapi.jar:${GENYRIS_HOME}/dist/mail.jar:${GENYRIS_HOME}/dist/opencsv-1.8.jar:${GENYRIS_HOME}/dist/org.json.jar:${GENYRIS_HOME}/dist/pop3.jar:${GENYRIS_HOME}/dist/servlet-api.jar:${GENYRIS_HOME}/dist/smtp.jar:${GENYRIS_HOME}/needed/bsf.jar:${GENYRIS_HOME}/needed/checkstyle-5.3-all.jar:${GENYRIS_HOME}/needed/commons-io-2.4.jar:${GENYRIS_HOME}/needed/commons-logging-1.1.jar:${GENYRIS_HOME}/needed/dsn.jar:${GENYRIS_HOME}/needed/failureaccess-1.0.3.jar:${GENYRIS_HOME}/needed/guava-33.4.8-jre.jar:${GENYRIS_HOME}/needed/hamcrest-core-1.3.jar:${GENYRIS_HOME}/needed/httpclient-4.5.1.jar:${GENYRIS_HOME}/needed/httpcore-4.4.3.jar:${GENYRIS_HOME}/needed/imap.jar:${GENYRIS_HOME}/needed/jline-2.11.jar:${GENYRIS_HOME}/needed/junit.jar:${GENYRIS_HOME}/needed/mailapi.jar:${GENYRIS_HOME}/needed/mail.jar:${GENYRIS_HOME}/needed/opencsv-1.8.jar:${GENYRIS_HOME}/needed/org.json.jar:${GENYRIS_HOME}/needed/pop3.jar:${GENYRIS_HOME}/needed/servlet-api.jar:${GENYRIS_HOME}/needed/smtp.jar:${GENYRIS_HOME}/src/resources" \
org.genyris.interp.ClassicReadEvalPrintLoop $*
