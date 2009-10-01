#
# Examples run as tests
#
define example-files 
   list
    ~ "examples/spawn-tests.g"
    ~ "examples/account.g"
    ~ "examples/class-methods.g"
    ~ "examples/class-variables.g"
    ~ "examples/context-sensitive-syntax.g"
    ~ "examples/cps.g"
    ~ "examples/executable-comments.g"
    ~ "examples/file-module-prefixes.g"
    ~ "examples/fun-with-brackets.g"
    ~ "examples/lambda.g"
    ~ "examples/length.g"
    ~ "examples/meta-classes.g"
    ~ "examples/module.g"
    ~ "examples/new.g"
    ~ "examples/module.g"
    ~ "examples/parametric-polymorphism.g"
    ~ "examples/people.g"
    ~ "examples/private_data.g"
    ~ "examples/symbols-as-objects.g"
    ~ "examples/triples.g"
    ~ "examples/unixscript.g"
    ~ "examples/url.g"
    ~ "examples/validate.g"
    ~ "examples/queens.g"
define pass nil
define results ()
while example-files
    catch errors
        include (left example-files)
    if errors
        results = (cons (cons (left example-files) errors) results)
        pass
    example-files = (right example-files)
if results
   raise results
   nil

queens 5