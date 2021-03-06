#
# Rosetta Code
#
include "lib/classify.g"

class AbstractQueue()
   def .valid?(object)
      object
         and
            bound? ^.enqueue
            is-instance? .enqueue Closure
            bound? ^.dequeue
            is-instance? .dequeue Closure

class XYZqueue(Object)
    def .init()
        var .items ()
    def .enqueue(object)
        setq .items (cons object .items)
    def .dequeue()
        var tmp  (car .items)
        setq .items (cdr .items)
        tmp

var my-queue (XYZqueue(.new))
print (my-queue.vars)
print (my-queue.classes)
print
   AbstractQueue(.valid? my-queue)
is? my-queue AbstractQueue
assert
   classify AbstractQueue my-queue
my-queue
  .enqueue 12
print my-queue
my-queue
  .dequeue
print my-queue

