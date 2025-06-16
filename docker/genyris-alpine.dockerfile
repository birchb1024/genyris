#
# Genyris presents a new programming paradigm. Objects can 
# belong to multiple classes independant from construction. Indentation reduces parentheses 
# yet retains the power of Lisp. External Java libraries can be imported.
#
#  https://github.com/birchb1024/genyris
#
# Make the docker image:
#
#   $ docker build -f docker/genyris-alpine.dockerfile --tag birchb1024/genyris-alpine:latest --tag birchb1024/genyris-alpine:$(git describe --tags) .
# 
#  REPL:
#
#	$ docker run -it genyris-alpine
#
#  Simple web server demo:
#
#	$ docker run -w /genyris -it -p 8000-8008:8000-8008 --name www-all \
#						genyris-alpine:0.9.4.1 bin/genyris examples/www-all.g
# 
#  Serve documentation
#
#    $ docker run -w /genyris -it -p 8000:8000 --name genyris-docs \
#						genyris-alpine:0.9.4.1 bin/genyris examples/web-server.g 8000 /genyris/dist
#
#  Self-tests:
#
#	$ docker run -it -w /genyris genyris-alpine bin/genyris -eval 'sys:self-test'
#

FROM openjdk:8-alpine
RUN apk update && apk add bash && apk add xvfb
ADD docker/genyris-alpine.dockerfile /Dockerfile
ADD dist/ /genyris/dist/
ADD bin/ /genyris/bin/
ADD examples/ /genyris/examples/
ADD lib/ /genyris/lib/
ADD test/ /genyris/test/
RUN /bin/bash -c 'echo Alpine $(cat /etc/alpine-release) && \
	java -version 2>&1 && \
	/genyris/bin/genyris < /dev/null'

USER 1000:1000
CMD genyris/bin/genyris

