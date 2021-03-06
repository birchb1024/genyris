> defvar ^f1 (.x) (cons .x .x)

*** Error: unbound variable: .x

> defvar ^f1 (lambda (.x) (cons .x .x))

~ <org.lispin.jlispin.interp.ClassicFunction>
> f1 3

*** Error: unbound variable: .x

> defvar ^.x "global .x"

~ "global .x"
> f1 3

"global .x" ^ "global .x"
> defvar ^f2 (lambda (x) (defvar ^.y "my .y") (cons .x .y))

~ <org.lispin.jlispin.interp.ClassicFunction>
> f2 22

"global .x" ^ "my .y"
> defvar ^f2 (lambda (x) (defvar ^.y "my .y") (cons .y (f3)))

~ <org.lispin.jlispin.interp.ClassicFunction>
> defvar ^f3 (lambda () .y)

~ <org.lispin.jlispin.interp.ClassicFunction>
> f2 44

"my .y" ^ "my .y"
> dict (.y "yyyyy")
     f3

~ <CallableEnvironment<dict ((f3 nil) (.y "yyyyy"))>>
> (dict (.y "yyyyy")) f3

~ <org.lispin.jlispin.interp.ClassicFunction>
> ((dict (.y "yyyyy")) f3)

~ "my .y"
> defvar ^d (dict (.y "yyyyy"))

~ <CallableEnvironment<dict ((.y "yyyyy"))>>
> d(f3)

~ "yyyyy"
> f3

~ "my .y"
> the .y

~ "my .y"
> ((dict (.y "yyyyy")) (f3))


> java.lang.ClassCastException
	at org.lispin.jlispin.interp.Evaluator.eval(Evaluator.java:17)
	at org.lispin.jlispin.interp.Interpreter.evalInGlobalEnvironment(Interpreter.java:61)
	at org.lispin.jlispin.interp.ClassicReadEvalPrintLoop.main(ClassicReadEvalPrintLoop.java:40)


(dict (.y "yyyyy") (f3)




)

~ nil
> (dict (.y "yyyyy") (f3)



)

~ nil
> (dict (.y "yyyyy")) (f3)

~ "yyyyy"
> the .y

~ "my .y"
> defvar ^f4 (lambda (.x) (cons .x (f5)))

~ <org.lispin.jlispin.interp.ClassicFunction>
> defvar ^f5 (lambda () .x)

~ <org.lispin.jlispin.interp.ClassicFunction>
> f5

~ "global .x"
> f4 9

"global .x" ^ "global .x"
> the .x

~ "global .x"
> 
> d (defvar ^.x ".x in d")

~ ".x in d"
> the d

~ <CallableEnvironment<dict ((.y "yyyyy") (.a 34) (.x ".x in d"))>>

> defvar ^f6 (lambda (.z) (f7))

~ <org.lispin.jlispin.interp.ClassicFunction>
> defvar ^f7 (lambda () .z)

~ <org.lispin.jlispin.interp.ClassicFunction>
> f6 4545

*** Error: unbound variable: .z

(dict (.z "temp .z")) (defvar ^fo (lambda () .z)) ((lambda () (fo))) # ==> "temp .z"
