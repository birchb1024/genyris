def source_dump(some_object)
    ~" Function to print source code of an objects dynamic variables if it exists.
    Examples:
        > define fl (cons (lambda () 1) (lambda () 2))
        :
        (<EagerProc: <anonymous lambda>> = <EagerProc: <anonymous lambda>>) # Pair
        > source_dump fl
        :
        ~ '.left => (lambda nil 1)\n'
        ~ '.right => (lambda nil 2)\n'
    "
    for varname in some_object!vars
        define the_value (some_object (symbol-value varname))
        #print varname the_value (use the_value .source)
        cond
            (use the_value (bound? ^.source))
                (print ("%a => %a\n"(.format varname (use the_value .source))))
