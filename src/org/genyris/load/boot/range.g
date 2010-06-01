#
# Ranges of integers
#
@prefix sys "http://www.genyris.org/lang/system#"


class Range()
   defmethod .mkIterator()
      define counter (- (nth 0 this) 1)
      define max (nth 1 this)
      tag RangeIterator
         lambda()
             cond
                (equal? counter max) ^sys:StopIteration
                else
                   counter = (+ counter 1)
                   counter
#
def range(low high)
   tag Range
      list low high
