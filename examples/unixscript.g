#!/usr/bin/sh /opt/home/birchb/workspace/genyris/bin/genyris
@prefix sys "http://www.genyris.org/lang/system#"

cond
    (bound? ^sys:argv)
        print sys:argv
