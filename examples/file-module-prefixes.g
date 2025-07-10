## File Handling Module (using prefixes)

@ns file "http://files.org/"


def file:copy(from to) etc
def file:delete(filename) etc
def file:zip(file) etc


## Use of the file module
@ns f "http://files.org/"

def archive(filename)
    f:copy filename "/tmp/foo"
    f:zip "/tmp/foo"
    f:delete filename