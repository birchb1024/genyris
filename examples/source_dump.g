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

def as-assoc (some_object)
    ~" Render some object and its dynamic variables as an assoc
    Examples:
        
        > as-assoc (dict (.a=2))
        :
        ((.a = 2) (.self = (dict (.a = 2))) (.vars .a .self .vars .classes) (.classes <class Dictionary (Builtin)>)) # Pair
        > as-assoc ^(1)
        :
        ((.line-number = 382) (.filename = 'console') (.left = 1) (.right) (.self 1) (.vars .line-number .filename .left .right .self .vars .classes) (.classes <class PairSource (Pair)>)) # Pair
    "
    map-left some_object!vars
        lambda (x) 
            cons x 
                some_object (symbol-value x)

