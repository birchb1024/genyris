## Copyright 2009 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
#
#  Python-style modules using a dictionary for scope
#
(define file (dict))
    define .name  "File Handling Functions"
    define .version "1.2"
    def .copy (from to) etc
    def .delete (filename) etc
    def .zip (file) etc

# usage
def archive(filename)
    (file.copy) filename "/tmp/foo"
    (file.zip) "/tmp/foo"
    (file.delete) filename

def archive2(filename)
    file!copy filename "/tmp/foo"
    file!zip "/tmp/foo"
    file!delete filename
    