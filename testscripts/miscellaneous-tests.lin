#
# Miscellaneous tests
#

var properties (System(.getProperties))
var OS-name (properties.|os.name|)
print OS-name
cond
    (OS-name(.match "Windows.*"))
        Sound(.play "testscripts/boing.wav")
        System(.exec "c:\\winnt\\system32\\cmd.exe" "/c" "date" "/t")
    (OS-name(.match "Linux.*"))
        System(.exec "/bin/date")
    (OS-name(.match "SunOS.*"))
        System(.exec "/usr/bin/date")
    else
        raise "unknown operating system"

