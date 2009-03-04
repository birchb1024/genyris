package org.genyris.io;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ReadFunction extends ApplicableFunction {

    public ReadFunction(Interpreter interp) {
    	super(interp, "read", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException  {
        if( arguments.length > 0)
            throw new GenyrisException("Too many arguments to read: " + arguments.length);
        InStream input = new UngettableInStream(
                            new ConvertEofInStream(
                               new IndentStream(
                                 new UngettableInStream(
                                     new StdioInStream()), true)));
        Parser parser = _interp.newParser(input);
        return parser.read();
    }

}
