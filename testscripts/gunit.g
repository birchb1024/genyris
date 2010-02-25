@prefix t "http://www.genyris.org/lib/gunit#"
@prefix u "http://www.genyris.org/lang/utilities#"

# TODO rewrite

df t:test (id &rest cases)
   #u:format "testing %a%n" id
   var passed-count 0
   var number-of-tests 0
   var result true
   while cases
       number-of-tests = (+ 1 number-of-tests)
      ##u:format "condition:  %s%n" (left cases)
       result =
            and (eval (left cases)) result
       cases = (right cases)
       cond
          result
            passed-count = (+ passed-count 1)
   u:format "Tests %a %a/%a passed%n" id passed-count number-of-tests
   result

defmacro t:given (expression keyword result)
    var actual (eval expression)
    ##u:format "given: %a got: %a expected: %a%n" =
        expression actual result
    template
      cond
        (equal? ,actual ,result)
           ##u:format "   Passed: %a%n" ^,expression
            true
        else
            u:format "  Condition %a failed. %n     given   %a%n     expected: %a %n     got: %a%n" id ^,expression ^,result ,actual
            nil

