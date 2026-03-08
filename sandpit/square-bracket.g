#
# graph[] -> subjects
# graph[<symbol>] ->
#
Graph
    def .asAssoc()
        map-left (.self(.asTriples))
            lambda(x)
                cons x!predicate x!object

    def .squareBracket (&rest args)
        var len (length args)
        cond
            (equal? 0 len)
                (.subjects)
            (equal? 1 len)
                tag Alist
                    (.select (nth 0 args) nil nil)(.asAssoc)
            (equal? 2 len)
                apply .get-list args
            (equal? 3 len)
                apply .select args

Alist
    var .squareBracket  Alist!lookup

Pair
    def .squareBracket (&rest args)
        apply Pair!slice args
    def .slice (&rest args)
        cond
            (equal? 1 (length args))
                nth (left args) .self
            (equal? 2 (length args))
                list @FILE @LINE 'TODO slice' args

def squareBracket (&rest args)
    apply .squareBracket args
