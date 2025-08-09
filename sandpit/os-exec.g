print
    os!exec 'date' '-u' '+%s'

print
    os!exec ^('date' '-u' '+%s')

print
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES')

print
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES') ^(('WIGGLES' 'BIG RED CAR'))

print
    os!exec ^('/bin/bash' '-c' 'echo $WIGGLES') ^(('WIGGLES'))
