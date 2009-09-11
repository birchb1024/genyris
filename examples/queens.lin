####
#### Taken from "Lisp" by Winston & Horn 2nd edition
#### problem 11-9
####
@prefix sys "http://www.genyris.org/lang/system#"

def queens (size)
  queen-aux nil 0 size

def queen-aux (board n size)               # start on next row
   cond
      (equal? n size)
         board-print (reverse board)
      else
         queen-sub board n 0 size

def queen-sub (board n m size)
   cond
      (equal? m size)
      else
         cond
            (conflict n m board)
            else
               queen-aux (cons (list n m) board) (+ n 1) size
         queen-sub board n (+ m 1) size

def conflict (cn cm cboard)
   cond
      (null? cboard) nil
      (or (threat cn cm (caar cboard) (cadar cboard))  (conflict cn cm (cdr cboard)))

def threat (i j a b)
   or
      equal? i a
      equal? j b
      equal? (- i j) (- a b)
      equal? (+ i j) (+ a b)


def board-print (board)
   board-print-aux board (length board)

def board-print-aux (board size)
   display "\n"
   cond
      (null? board)
      else
         board-print-sub (cadar board) 0 size
         board-print-aux (cdr board) size

def board-print-sub (column n size)
   cond
      (equal? n size)
      else
         cond
            (equal? column n)
               display ^Q
            else
               display "."
         board-print-sub column (+ n 1) size

##
## If invoked as a script use first arg as size of board
cond
    (and (bound? ^ sys:argv) sys:argv)
        var size-of-board ((ParenParser(.new (cadr sys:argv)))(.read))
        queens size-of-board