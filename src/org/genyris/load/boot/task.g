#
#
#
@ns sys 'http://www.genyris.org/lang/system#'
@ns task 'http://www.genyris.org/lang/task#'

class Task()
    def .kill()
        task:kill .id

#eval # patch to get Task not Dictionary
#   template
#      def task:id()
#         tag Task ($(the task:id))
#TODO - wonder what the above was for. delete?
#
var sleep task:sleep

def find-abs-path((filename = String))
   cond
      (isAbsolutePath? filename) filename
      else
         sys:search-path filename

def spawn(&rest args)
    print (list @FILE @LINE args)
    cond
      (null? args)
          raise 'No arguments to spawn!'
    var path (find-abs-path args!left)
    cond
        (null? path)
          raise ('spawn script not found: %s'(.format args!left))
    tag Task
        apply task:spawn
          cons path args!right

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

