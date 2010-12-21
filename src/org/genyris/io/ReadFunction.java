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
				new UngettableInStream(StdioInStream.knew()), true)));
		parser = _interp.newParser(input);
		parser.setUsualPrefixes();

	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		if (arguments.length > 0)
			throw new GenyrisException("Too many arguments to read: "
					+ arguments.length);
		return parser.read();
	}

	public static void bindFunctionsAndMethods(Interpreter interpreter)
			throws UnboundException, GenyrisException {
		interpreter.bindGlobalProcedureInstance(new ReadFunction(interpreter));
	}
}
