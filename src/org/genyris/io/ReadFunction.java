package org.genyris.io;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class ReadFunction extends ApplicableFunction {
    private InStream input;
    private Parser parser;

    public ReadFunction(Interpreter interp) throws GenyrisException {
        super(interp, "read", true);
        input = new UngettableInStream(new ConvertEofInStream(new IndentStream(
                new UngettableInStream(_interp.getInput()), true)));
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        //
        // (read true) gets line numbers via PairSource objects
        //
        if (arguments.length == 0) {
            parser = new Parser(_interp.getSymbolTable(), input);
        } else if (arguments.length == 1) {
            if( ! arguments[0].isNil()) {
                parser = new ParserSource(_interp.getSymbolTable(), input);
            } else {
                throw new GenyrisException("nil argument to read.");
            }
        } else {
            throw new GenyrisException("too many arguments to read: "
                    + arguments.toString());
        }
        parser.setUsualPrefixes(_interp);
        return parser.read(envForBindOperations);
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter)
            throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new ReadFunction(interpreter));
    }
}
