#
# Container for building, packaging Genyris code and documenation
#
# Build-the-builder:
#
#   $ docker build -f docker/genyris-build.dockerfile --tag genyris-build .
#
# Use the builder:
#
#   The Install4j license key must be in the environment variable INSTALL4J_LICENSE.
#
#   Acquire the source code to be built into a 'genyris' directory...
#
#   $ git clone git@github.com:birchb1024/genyris.git
#
#   Now run the build in the current directory using the tools in the container. This
#   runs as user id 1000 aka 'builder'.  
#
#   $ docker run --rm --user $(id -u):$(id -g) -it -e INSTALL4J_LICENSE=${INSTALL4J_LICENSE} -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v "${PWD}":/genyris -w /genyris genyris-build
#
FROM debian:9-slim

# Add tools from the Debian package world..
ENV DEBIAN_FRONTEND=noninteractive
RUN mkdir -p /usr/share/man/man1 # Needed by openjdk-8-jdk !
RUN apt-get -qq update && \
    apt-get -qq install -y xvfb vim git openjdk-8-jdk ant lyx elyxer

# Add other, non-packaged tools..
ADD tools/install4j_linux_7_0_3.deb /install4j_linux_7_0_3.deb
RUN dpkg -i /install4j_linux_7_0_3.deb && \
    install4jc --license='L-M7-GENYRIS#50040871010001-2rbsvr4yqnx2gc#738b6'

# Set up the non-root user
RUN groupadd --gid 1000 builder && useradd -m --uid 1000 --gid 1000 builder

RUN java -version && \
    ant -version && \
    git --version && \
    install4jc --version

USER builder:builder
CMD ant

