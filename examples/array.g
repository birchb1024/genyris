#
#  Use Graph as a sparse array
#
class Array(Graph)
    def .new (&rest args)
        var array (graph)
        var index 0
        for item in args
            array
                .add
                    triple item ^_ index
            setq index (+ index 1)
        tag Array array

    def .index (i)
        var result nil
        def check-triple (s p o)
            setq result s
        var query (.select nil nil i check-triple)
        cond 
            (not (equal? 1 (query(.length))))
                raise ('index not found %s'(.format i))
        result

    def .setq (i v)
        .select nil nil i 
            function (s p o)
                .remove (triple s p o)
        .put v ^_ i

    # TODO 
    # def .next ()
    #   # iterator for use in 
    #   # for v in (.next)
    #   #   print v
    #

    def .assoc ()
        var result ()
        def mk-assoc (s p o)
            setq result (cons (cons o s) result)
        var query (.select nil nil nil mk-assoc)
        result

    defmethod .list ()
        var result ()
        def mk-list (s p o)
            setq result (cons s result)
        for i in (range 0 (this!length))
            .select nil nil i mk-list
        reverse result # TODO rewrite using a .next iterator


define squareBracket Array!new
        

# Tests
var an-array [1 2 3 4]

assertEqual 1
    an-array(.index 0)

an-array(.setq 0 99)

assertEqual 99
    an-array(.index 0)

assertEqual (an-array(.list)) ^(99 2 3 4)

var a [^(a=3) 33 'eee']
assertEqual ^(a=3)
    a(.index 0)
assertEqual 33
    a(.index 1)

assertEqual (a(.list)) ^((a=3) 33 'eee')

[1 2 nil 3](.assoc)
