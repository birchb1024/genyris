defvar ^$global 999

defvar ^Standard-Class (dict)

defvar ^Account
    dict
        .classes = (list Standard-Class)
        .print =
            lambda () (list $global .balance)

Account
    defvar ^.new
        lambda (initial-balance)
          dict
            .classes (cons Account nil)
            .balance initial-balance

defvar ^bb 
  Account (.new 1000) 

bb(.print)

bb((lambda () .balance))




