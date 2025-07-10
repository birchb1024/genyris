#!/usr/bin/sh /opt/home/birchb/workspace/genyris/bin/genyris
@ns sys "http://www.genyris.org/lang/system#"

cond
    (bound? ^sys:argv)
        print sys:argv
