#
# Miscellaneous tests
#
import Sound
var properties (os(.getProperties))
var OS-name (properties.|os.name|)
Sound(.play (prepend-home "test/fixtures/boing.wav"))
Sound(.play (prepend-home "test/fixtures/boing.wav"))
print OS-name
cond
    (OS-name(.match "Windows.*"))
        Sound (.play (prepend-home "test/fixtures/boing.wav"))
        var command 
            "%a\\system32\\cmd.exe"
                  .format (os!getenv 'SystemRoot')
        print
            os(.exec command "/c" "date" "/t")
    (OS-name(.match "Linux.*"))
        print (os(.exec "/bin/date"))
    (OS-name(.match "SunOS.*"))
        print (os(.exec "/bin/date"))
