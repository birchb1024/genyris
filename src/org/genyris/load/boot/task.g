#
#
#
@prefix task 'http://www.genyris.org/lang/task#'

class Task()
    def .kill()
        task:kill .id

eval # patch to get Task not Dictionary
   template
      def task:id() 
         tag Task ($(the task:id))
var sleep task:sleep
    
def spawn(&rest args)
    tag Task
        apply task:spawn args

def httpd(&rest args)
    tag Task
        apply task:httpd args

def ps()
    # Tag all items as Tasks
    var result (task:ps)
    for p in result
       tag Task p
    result
    
def id()
    tag Task (task:id)

