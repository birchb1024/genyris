#
# Tests for various kinds of iterators and macros.
#

@prefix u "http://www.genyris.org/lang/utilities#"
@prefix sys "http://www.genyris.org/lang/system#"

# Ranges

def test-nested-range (actual)
   for x in (range 1 3)
     for y in (range 5 7)
        #u:format "%s %s\n" x y
        actual = (cons (list x y) actual)
assert (equal? (test-nested-range nil) ^((3 7) (3 6) (3 5) (2 7) (2 6) (2 5) (1 7) (1 6) (1 5)))

def test-computed-range(actual)
   define X ^(a b c)
   for x in (range 0 (- (length X) 1))
      actual = (cons (list x (nth x X)) actual)
      #trace actual
assert (equal? (test-computed-range nil) ^((2 c)(1 b) (0 a)))

# Dictionary

var D (dict (.a=1)(.b=2))  
  
for v in D
   u:format "%s = %s\n" v (D (symbol-value v))

# Lists

define L ^(1 2)
define iter (L(.mkIterator))
assert
   equal?
      list (iter) (iter) (iter)
      ^(1 2 sys:StopIteration)


for v in L
  print v
#
define n ^(1 2)
define z ^(A B)
for v1 in n
   for v2 in z
        u:format "%s %s\n" v1 v2
#
define n ^(1 2)
for v1 in n
   for v1 in n
        u:format "%s\n" v1


