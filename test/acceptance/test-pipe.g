#
# Pipes Examples
#
@prefix u "http://www.genyris.org/lang/utilities#"
for pipe in (Pipe!list)
   Pipe!delete pipe
def killall(subst)
   for task in (ps)
        cond
           (task!name (.match subst))
               u:format "killing %s\n" task
               task(.kill)
#
# Write and read within the same task:
#
define apipe (Pipe!open 'mypipe')
assert (equal? ('%s' (.format apipe)) '[Pipe: mypipe]')
Pipe!list
define out
   apipe(.output)
out(.format 'Hello from the output\n')
out(.flush)


define inpipe apipe
assert (equal? inpipe apipe)
define in (inpipe(.input))
assert (equal? 'Hello from the output' (in(.getline)))
Pipe!delete 'mypipe'
assert (equal? (Pipe!list) nil)

#
# share a text line using a pipe between this task and a reading task    
#
define shared (Pipe!open 'shared-pipe')
spawn 'test/mocks/pipe-reader.g' 'shared-pipe'
define out
   shared(.output)
out(.format 'Hello from the first task\n')


#
# single reader, multiple writers - scrambled together at reader since no synchronisation
#
define shared (Pipe!open 'shared-pipe2')
for i in (range 0 5)
    spawn 'test/mocks/pipe-writer.g' 'shared-pipe2'
define in (shared(.input))
for i in (range 0 50)
   in(.getline)
   u:format "."
u:format "\n"

#         
# single reader, multiple writers - with synchronisation
#
define shared (Pipe!open 'shared-pipe3')

for i in (range 0 5)
    spawn 'test/mocks/pipe-writer-synch.g' 'shared-pipe3'
define in (shared(.input))
for i in (range 0 100)
   var line (in(.getline))
   assert (equal? 'Hello from task.' line)
killall 'test/mocks/pipe-writer-synch.g'
       
print
   Pipe!list

#
# Write s-expression and parse expression at receiver.
#
define shared (Pipe!open 'expression-pipe')
spawn 'test/mocks/pipe-expression-reader.g' 'expression-pipe'
define out
   shared(.output)
out(.format '123 456 "abc"')
out(.format '%s' ^(1 W (x=y) 'z' .a 23.45))
out(.format '%s' ^(def foo(a) (cons a a)))

print
   Pipe!list
killall '.*pipe.*'

