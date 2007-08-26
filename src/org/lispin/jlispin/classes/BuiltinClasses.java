package org.lispin.jlispin.classes;

import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.Environment;

public class BuiltinClasses {

	public static Lobject OBJECT;
	public static Lobject PAIR;;
	public static Lobject INTEGER;
	public static Lobject BIGNUM;
	public static Lobject STRING;
	public static Lobject DOUBLE;
	public static Lobject SYMBOL;
    public static Lobject PRINTWITHCOLON;
    public static Lobject STANDARDCLASS;

	private static Lobject mkClass(String name, Lsymbol nil) {
		return new Lobject(SymbolTable.classname, new Lsymbol(name), nil ); // TODO should tis symbol be interned?
	}
	public static void init(Environment env) {
		Lsymbol NIL = env.getNil();
		PAIR = mkClass("Pair", NIL);
		OBJECT = mkClass("Object", NIL);
		INTEGER = mkClass("Integer", NIL);
		BIGNUM = mkClass("Bignum", NIL);
		STRING = mkClass("String", NIL);
		DOUBLE = mkClass("Double", NIL);
		SYMBOL = mkClass("Symbol", NIL);
        PRINTWITHCOLON = mkClass("PRINTWITHCOLON", NIL);
        STANDARDCLASS = mkClass("StandardClass", NIL);	
	}
}
