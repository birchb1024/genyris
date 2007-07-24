package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.classes.BuiltinClasses;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class DefineClassFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
        if( arguments.length < 1) 
            throw new LispinException("Incorrect number of arguments to class.");
        if(! (arguments[0] instanceof Lsymbol)) {
            throw new LispinException("class expects a symbols.");
        }
        Exp klassname = arguments[0];
        Lobject newClass = new Lobject();
        newClass.defineVariable(SymbolTable.classname, klassname);
        newClass.defineVariable(SymbolTable.classes
                    , new Lcons(BuiltinClasses.STANDARDCLASS , SymbolTable.NIL));
        if( arguments.length > 1) {
            Exp superklasses = arguments[1];            
            newClass.defineVariable(SymbolTable.superclasses, lookupClasses(env, superklasses));
        }
        env.defineVariable(klassname, newClass);
        return newClass;
    }
    private Exp lookupClasses(Environment env, Exp superklasses) throws LispinException {
        Exp result = SymbolTable.NIL;
        while(superklasses != SymbolTable.NIL) {
            result = new Lcons(env.lookupVariableValue(superklasses.car()), result);
            superklasses = superklasses.cdr();
        }
        return result;
    }
    
    public Object getJavaValue() {
        return "<the class builtin function>";
    }

}
