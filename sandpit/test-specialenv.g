

defvar ^c ((lambda (.x) (closure)) 34)

c.x

def some-function (x) (closure)

defvar ^my-closure (some-function 99)

my-closure .x

defvar ^d ((lambda (.y) (closure)) c)

c (set ^.x 99)

c.x

c (defvar ^.class ^foo)

c (cons 1 2) (the .class)
c
   cons 1 2
   .self .class
   cons .x .class

c (defvar ^.method (lambda (y) (cons .x y)))

c (.method 44)

defvar ^d (c .self)

d .x

(lambda (z) (c z) 67
(lambda (z) (c z)) 88
(lambda (z) (c (cons z .x))) 90   # 90 . 99

defvar ^Person ((lambda (.name) (closure)) ^Person)
Person (defvar ^.new (lambda (.name) (defvar ^.class .self) (closure)))
Person
  defvar ^.new
    lambda (.name)
       defvar ^.class .self
       closure
defvar ^peeps
   Person (.new "fred")

