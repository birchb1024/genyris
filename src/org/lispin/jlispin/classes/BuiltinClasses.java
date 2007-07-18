package org.lispin.jlispin.classes;

import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;

public class BuiltinClasses {

	public static Lobject PAIR;;
	public static Lobject INTEGER;
	public static Lobject STRING;
	public static Lobject DOUBLE;
	public static Lobject SYMBOL;

	private static Lobject mkClass(String name) {
		return new Lobject(SymbolTable.classname, new Lcons(new Lsymbol(name),SymbolTable.NIL));
	}
	public static void init() {
		PAIR = mkClass("PAIR");
		INTEGER = mkClass("INTEGER");
		STRING = mkClass("STRING");
		DOUBLE = mkClass("DOUBLE");
		SYMBOL = mkClass("SYMBOL");
		
	}
}
