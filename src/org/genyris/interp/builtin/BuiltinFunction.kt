package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.load.LoadFunction

abstract class BuiltinFunction(interp: Interpreter, name: String?, eager: Boolean) :
    ApplicableFunction(interp, name, eager) {
    @kotlin.Throws(GenyrisException::class)
    abstract override fun bindAndExecute(
        proc: Closure?,
        arguments: Array<Exp?>?,
        envForBindOperations: Environment?
    ): Exp?

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(ApplyFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(TailCallFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(AsStringFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(BackquoteFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(BoundFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(CarFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(CatchFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(CdrFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ConditionalFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ConsFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(DefFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(DefineClassFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(DefineFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(DefMacroFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(DynamicSymbolValueFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(EqFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(EqualsFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(EvalFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(GensymFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(HashCodeFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(IdentityFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(IsFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(InternFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(LambdaFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(LambdamFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(LambdaqFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(LengthFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ListFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(LoadFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(NthFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ObjectFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(QuoteFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(RaiseFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(RemoveTagFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ReplaceCarFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ReplaceCdrFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ReverseFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SetFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(PlingFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SortFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SymbolValueFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SymbolPrefixFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SymListFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(TagFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(WhileFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(BackTraceFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(UseFunction(interpreter))
        }
    }
}
