#
# Script to prove that System.exit() does not actually work immediately, whereas Runtime halt() does.
#
@ns sys "http://www.genyris.org/lang/system#"

cond
    (equal? 2 (length sys:argv))
        print (list @LINE (nth 1 sys:argv))
        os!halt 0
        some-task
        sleep (* 60 1000)
        (id)(.kill)
    (equal? 1 (length sys:argv))
        for i in (range 1 10)
            spawn (nth 0 sys:argv) i
        while (> (length (ps)) 1)
            stdout(.flush)
            print ('%a waiting for finish, task count %s...'(.format @LINE (length (ps))))
            sleep (* 2 1000)

def some-task()
    var fd
       (File(.new (prepend-home 'sandpit/atmosphere.lsp')))
          .open ^read
    var parser (ParenParser(.new fd))
    write
      parser (.read)
