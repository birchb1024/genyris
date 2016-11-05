#
# Reverse-Polish pocket Calculator
#
#
@prefix u "http://www.genyris.org/lang/utilities#"


define prompt "? "

#
# The execution stack
#
define stack ^()

def top() (car stack)

def push(item)
    setq stack (cons item stack)

def pop()
    cond
        (null? stack) nil
        else
            define ret (car stack)
            setq stack (cdr stack) 
            ret

def stack-nth (n)
    n stack

def exchange () 
   var tmp1 (pop) 
   var tmp2 (pop)
   push tmp1
   push tmp2

#
# A register of variables
#
define variables (graph)

def set-var (varname val)
    variables(.put ^this varname val)
    val

def print-vars (vlist)
    u:format 'Variables: ' 
    for trip in ((variables(.select ^this nil nil))(.asTriples))
        u:format '%s = %s, ' trip!predicate trip!object
    u:format '\n' 

#
# User command definition
#
define commands
  tag Alist ^()

df define-command (label help-text &rest body)
    u:format '  %a %a\n' label help-text
    define tmpfunc
        eval
            template
                lambda () $@body
    setq commands
        cons (cons label tmpfunc) commands
    tag Alist commands

define-command x "eXchange the top two stack elements."
    exchange

define-command p "Pop the stack." (pop)

define-command s "Store to variable." 
    set-var (nth 0 stack) (nth 1 stack)
    pop
    pop

define-command r "Recall variable value."
    push (variables(.get ^this (pop)))
    
define-command n "Push the nth stack item."
    push (nth (pop) stack)

define-command e "Push the top stack item."  
    push (top)

define-command q "Quit."  
    setq calc-finished t

define-command c "clear stack"
    setq stack nil

define-command clear "clear all"
    setq stack nil
    setq variables (graph)

define-command + "sum the two top stack items"
    push (+ (pop) (pop))

define-command - "difference the two top stack items" 
    exchange
    push (- (pop) (pop))

define-command * "multiply"
    push (* (pop) (pop))

define-command / "divide"
    exchange
    push (/ (pop) (pop))

define-command % "remainder" 
    push (% (pop) (pop))

#
# Calculator main loop
#
define calc-finished nil

def calc ()
    u:format prompt
    while (not (calc-finished))
        var input_line (read)
        cond
            (equal? input_line ^EOF)
                u:format  "Bye...\n"
                setq calc-finished true
            else
                for input in input_line
                    cond
                        (is-instance? input Bignum)
                            push input
                        (commands(.hasKey input))
                            apply (commands(.lookup input)) nil
                        (is-instance? input Symbol)
                            push input
                        else
                            u:format "Unknown command ignored %s\n" input
                print-vars variables
                print stack
                u:format prompt

calc
