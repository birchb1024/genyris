package org.lispin.jlispin.classes;

import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;

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

	private static Lobject mkClass(String name) {
		return new Lobject(SymbolTable.classname, new Lsymbol(name));
	}
	public static void init() {
		PAIR = mkClass("Pair");
		OBJECT = mkClass("Object");
		INTEGER = mkClass("Integer");
		BIGNUM = mkClass("Bignum");
		STRING = mkClass("String");
		DOUBLE = mkClass("Double");
		SYMBOL = mkClass("Symbol");
        PRINTWITHCOLON = mkClass("PRINTWITHCOLON");
        STANDARDCLASS = mkClass("StandardClass");
		
	}
}
