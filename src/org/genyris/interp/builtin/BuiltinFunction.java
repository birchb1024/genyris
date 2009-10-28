package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.load.LoadFunction;

public abstract class BuiltinFunction  extends ApplicableFunction {

    public BuiltinFunction(Interpreter interp, String name, boolean eager) {
        super(interp, name, eager);
    }

    public abstract Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException;

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new ApplyFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new AsStringFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new BackquoteFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new BoundFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new CarFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new CatchFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new CdrFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ConditionalFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ConsFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new DefFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new DefineClassFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new DefineFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new DefMacroFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new DynamicSymbolValueFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new EqFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new EqualsFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new EvalFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new GensymFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new IdentityFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new IsFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new InternFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new LambdaFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new LambdamFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new LambdaqFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new LengthFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ListFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new LoadFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new NthFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ObjectFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new QuoteFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new RaiseFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new RemoveTagFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ReplaceCarFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ReplaceCdrFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ReverseFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new SetFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new SymbolValueFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new SymListFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new TagFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new WhileFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new BackTraceFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new UseFunction(interpreter));
    }

}
