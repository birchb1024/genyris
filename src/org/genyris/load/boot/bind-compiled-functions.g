## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
@prefix s "http://www.genyris.org/lang/system#"
#
# Load extension functions
#     These have to be before any interpreted code since they define functions in the interpreter.
#
s:load-class-by-name "org.genyris.interp.builtin.BuiltinFunction"
s:load-class-by-name "org.genyris.io.writerstream.WriterStream$AbstractWriterMethod"
s:load-class-by-name "org.genyris.io.file.Gfile"
s:load-class-by-name "org.genyris.io.ReadFunction"
s:load-class-by-name "org.genyris.string.AbstractStringMethod"
s:load-class-by-name "org.genyris.io.readerstream.ReaderStream"
s:load-class-by-name "org.genyris.io.parser.StreamParser"
s:load-class-by-name "org.genyris.io.StringFormatStream"
s:load-class-by-name "org.genyris.dl.ThingMethods"
s:load-class-by-name "org.genyris.system.ExecMethod"
s:load-class-by-name "org.genyris.system.SystemPropertiesMethod"
s:load-class-by-name "org.genyris.system.SystemGetenvMethod"

s:load-class-by-name "org.genyris.interp.builtin.ObjectFunction"
s:load-class-by-name "org.genyris.dl.TripleFunctions"
s:load-class-by-name "org.genyris.dl.TripleStoreFunction"
s:load-class-by-name "org.genyris.logic.AbstractLogicFunction"
s:load-class-by-name "org.genyris.math.AbstractMathFunction"
s:load-class-by-name "org.genyris.format.AbstractFormatFunction"
s:load-class-by-name "org.genyris.task.TaskFunction"
s:load-class-by-name "org.genyris.web.HTTPgetFunction"
s:load-class-by-name "org.genyris.classification.IsInstanceFunction"
s:load-class-by-name "org.genyris.test.JunitRunnerFunction"
s:load-class-by-name "org.genyris.email.SendFunction"
s:load-class-by-name "org.genyris.system.ExitMethod"
s:load-class-by-name "org.genyris.system.SystemTicksMethod"
s:load-class-by-name "org.genyris.datetime.AbstractDateTimeFunction"

s:load-class-by-name "org.genyris.java.AbstractJavaMethod"
