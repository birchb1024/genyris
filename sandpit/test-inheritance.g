defvar ^Thing
   dict
      .toString "toString in Thing"
defvar ^C (dict (.print = 23) (.classes = (list Thing)))
defvar ^C2 (dict (.draw = 99))
defvar ^obj (dict (.classes = (cons C (cons C2 nil))))

defvar ^Standard-Class
    dict
defvar ^Base-1
    dict 
        .classes = (list Standard-Class)
        .toString = "Base-1 toString"
defvar ^Base-2
    dict 
        .classes = (cons Standard-Class nil)
        .log = "Base-2 log"
defvar ^Class-1
    dict
        .classes = (cons Standard-Class nil)
        .superclasses = (cons Base-1 nil)
        .print = "Class-1 print"
        .new
            lambda (.a)
                dict
                    .classes = (cons Class-1 nil)
                    .a = .a
defvar ^Class-2
    dict
        .classes = (cons Standard-Class nil)
        .superclasses = (cons Base-2 nil)
        .draw = "Class-2 draw"
defvar ^object 
    dict
      .classes = (cons Class-1 (cons Class-2 nil))


