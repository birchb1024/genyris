#
# Miscellaneous tests
#
import Sound
var properties (os(.getProperties))
var OS-name (properties.|os.name|)
print OS-name
cond
    (OS-name(.match "Windows.*"))
        Sound(.play "test/fixtures/boing.wav")
        os(.exec "c:\\winnt\\system32\\cmd.exe" "/c" "date" "/t")
    (OS-name(.match "Linux.*"))
        os(.exec "/bin/date")
    (OS-name(.match "SunOS.*"))
        os(.exec "/usr/bin/date")
    else
        raise "unknown operating system"

