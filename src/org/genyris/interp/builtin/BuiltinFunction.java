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
        interpreter.bindGlobalProcedure(ApplyFunction.class);
        interpreter.bindGlobalProcedure(BackquoteFunction.class);
        interpreter.bindGlobalProcedure(BoundFunction.class);
        interpreter.bindGlobalProcedure(CarFunction.class);
        interpreter.bindGlobalProcedure(CdrFunction.class);
        interpreter.bindGlobalProcedure(ConditionalFunction.class);
        interpreter.bindGlobalProcedure(ConsFunction.class);
        interpreter.bindGlobalProcedure(DefFunction.class);
        interpreter.bindGlobalProcedure(DefineClassFunction.class);
        interpreter.bindGlobalProcedure(DefineFunction.class);
        interpreter.bindGlobalProcedure(DefMacroFunction.class);
        interpreter.bindGlobalProcedure(DynamicSymbolValueFunction.class);
        interpreter.bindGlobalProcedure(EqFunction.class);
        interpreter.bindGlobalProcedure(EqualsFunction.class);
        interpreter.bindGlobalProcedure(EvalFunction.class);
        interpreter.bindGlobalProcedure(GensymFunction.class);
        interpreter.bindGlobalProcedure(IdentityFunction.class);
        interpreter.bindGlobalProcedure(InternFunction.class);
        interpreter.bindGlobalProcedure(LambdaFunction.class);
        interpreter.bindGlobalProcedure(LambdamFunction.class);
        interpreter.bindGlobalProcedure(LambdaqFunction.class);
        interpreter.bindGlobalProcedure(LengthFunction.class);
        interpreter.bindGlobalProcedure(ListFunction.class);
        interpreter.bindGlobalProcedure(LoadFunction.class);
        interpreter.bindGlobalProcedure(ObjectFunction.class);
        interpreter.bindGlobalProcedure(QuoteFunction.class);
        interpreter.bindGlobalProcedure(RaiseFunction.class);
        interpreter.bindGlobalProcedure(RemoveTagFunction.class);
        interpreter.bindGlobalProcedure(ReplaceCarFunction.class);
        interpreter.bindGlobalProcedure(ReplaceCdrFunction.class);
        interpreter.bindGlobalProcedure(ReverseFunction.class);
        interpreter.bindGlobalProcedure(SetFunction.class);
        interpreter.bindGlobalProcedure(SymbolValueFunction.class);
        interpreter.bindGlobalProcedure(SymListFunction.class);
        interpreter.bindGlobalProcedure(TagFunction.class);
        interpreter.bindGlobalProcedure(WhileFunction.class);




    }

}
