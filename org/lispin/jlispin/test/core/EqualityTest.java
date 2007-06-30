package org.lispin.jlispin.test.core;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Dict;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Ldouble;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.Lstring;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;

public class EqualityTest extends TestCase {
	
	public void testInt1() throws Exception {
		assertTrue(new Linteger(12).equals(new Linteger(12)));	
		assertFalse(new Linteger(11).equals(new Linteger(12)));
	}
	public void testDouble1() throws Exception {
		assertTrue(new Ldouble(12.23e9).equals(new Ldouble(12.23e9)));	
		assertFalse(new Ldouble(12.230001e9).equals(new Ldouble(12.23e9)));	
	}
	public void testString() throws Exception {
		assertTrue(new Lstring("hello").equals(new Lstring("hello")));	
		assertFalse(new Lstring("hello1").equals(new Lstring("hello2")));	
	}
	public void testCons() throws Exception {
		assertTrue(new Lcons(new Lstring("hello"), new Linteger(12)).equals(new Lcons(new Lstring("hello"), new Linteger(12))));	
		assertFalse(new Lcons(new Lstring("hello"), new Lstring("no way")).equals(new Lcons(new Lstring("hello"), new Linteger(12))));	
	}
	public void testSymbol() throws Exception {
		SymbolTable sym = new SymbolTable(); // force loadingof statics!
		assertTrue(SymbolTable.NIL.equals(SymbolTable.NIL));	
		assertFalse(new Lsymbol("hello1").equals(new Lsymbol("hello2")));	
		assertFalse(new Lsymbol("hello").equals(new Lsymbol("hello")));	
		assertTrue(sym.internString("hello").equals(sym.internString("hello")));
		assertFalse(sym.internString("hello").equals(new Lsymbol("hello")));
	}
	
	public void testHashMap() throws Exception {
		Map dict1 = new HashMap();
		dict1.put(new Double(123.345), new Integer(2));
		Map dict2 = new HashMap();
		dict2.put(new Double(123.345), new Integer(2));
		assertTrue("foo".equals("foo"));
		assertTrue(dict1.equals(dict2));
	}
	
	public void testFrame() throws Exception {
		Dict f1 = new Dict();
		f1.defineVariable(new Linteger(1), new Lstring("foo"));
		Dict f2 = new Dict();
		f2.defineVariable(new Linteger(1), new Lstring("foo"));
		assertTrue(f1.equals(f2));
	}
}
