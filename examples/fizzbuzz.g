#
# See: https://rosettacode.org/wiki/FizzBuzz
#
# Task
#
# Write a program that prints the integers from   1   to   100   (inclusive).
#
# But:
#
#  for multiples of three,   print   Fizz     (instead of the number)
#  for multiples of five,   print   Buzz     (instead of the number)
#  for multiples of both three and five,   print   FizzBuzz     (instead of the number)
#
# The   FizzBuzz   problem was presented as the lowest level of comprehension 
# required to illustrate adequacy.
#
@prefix u "http://www.genyris.org/lang/utilities#"

# The traditional version
def fizzbuzz (n)
    def multiple?(n x) (equal? 0 (% n x))
    cond
        (multiple? n 3)
            'fizz'
        (multiple? n 5)
            'buzz'
        (multiple? n 15)
            'fizzbuzz'
        else
            n

for n in (range 1 100)
    u:format "%a\n"
        fizzbuzz n

# A bit more functional
def fizzbuzz (n)
    map-left ^((3 = 'fizz') (5 = 'buzz'))
        lambda (d)
            cond
                (equal? 0 (% n d!left))
                    d!right
                else
                    ''
                        
for n in (range 1 100)
    define fb (''(.join (fizzbuzz n)))
    u:format "%a\n"
        cond 
            (equal? fb '') 
                n
            else
                fb
    

