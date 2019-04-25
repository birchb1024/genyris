## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix : "http://www.genyris.org/lang/system#"
#
# Load extension functions
#     These have to be before any interpreted code since they define functions in the interpreter.
#
:load-class-by-name "org.genyris.interp.builtin.BuiltinFunction"
:load-class-by-name "org.genyris.io.writerstream.WriterStream$AbstractWriterMethod"
:load-class-by-name "org.genyris.io.file.Gfile"
:load-class-by-name "org.genyris.io.ReadFunction"
:load-class-by-name "org.genyris.string.AbstractStringMethod"
:load-class-by-name "org.genyris.io.readerstream.ReaderStream"
:load-class-by-name "org.genyris.io.parser.StreamParser"
:load-class-by-name "org.genyris.io.parser.ParenStreamParser"
:load-class-by-name "org.genyris.io.parser.IndentedStreamParser"
:load-class-by-name "org.genyris.io.StringFormatStream"
:load-class-by-name "org.genyris.dl.ThingMethods"
:load-class-by-name "org.genyris.os.ExecMethod"
:load-class-by-name "org.genyris.os.SpawnMethod"
:load-class-by-name "org.genyris.os.SystemPropertiesMethod"
:load-class-by-name "org.genyris.os.SystemGetenvMethod"

:load-class-by-name "org.genyris.interp.builtin.ObjectFunction"
:load-class-by-name "org.genyris.dl.TripleFunctions"
:load-class-by-name "org.genyris.dl.GraphFunction"
:load-class-by-name "org.genyris.logic.AbstractLogicFunction"
:load-class-by-name "org.genyris.math.AbstractMathFunction"
:load-class-by-name "org.genyris.format.AbstractFormatFunction"
:load-class-by-name "org.genyris.task.TaskFunction"
:load-class-by-name "org.genyris.web.HTTPclientFunction"
:load-class-by-name "org.genyris.classification.IsInstanceFunction"
:load-class-by-name "org.genyris.test.JunitRunnerFunction"
:load-class-by-name "org.genyris.system.ExitMethod"
:load-class-by-name "org.genyris.os.SystemTicksMethod"
:load-class-by-name "org.genyris.datetime.AbstractDateTimeFunction"

:load-class-by-name "org.genyris.java.AbstractJavaMethod"

:load-class-by-name "org.genyris.io.pipe.Pipe"

:load-class-by-name "org.genyris.io.JlineUseEnvironmentFunction"
