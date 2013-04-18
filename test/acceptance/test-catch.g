


@prefix sys "http://www.genyris.org/lang/system#"


sys:backtrace
catch err
   raise 78
assertEqual 78 err
#assertEqual 1 (length (sys:backtrace))

sys:backtrace
catch (err bt)
   raise 78
assertEqual 78 err
print bt
#assertEqual 1 (length (sys:backtrace))