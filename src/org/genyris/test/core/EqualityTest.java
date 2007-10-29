// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;
import org.genyris.core.SymbolTable;
import org.genyris.interp.Interpreter;
import org.genyris.interp.StandardEnvironment;

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
		assertTrue(new Lcons(new Lstring("hello"), new Linteger(12)).deepEquals(new Lcons(new Lstring("hello"), new Linteger(12))));
		assertFalse(new Lcons(new Lstring("hello"), new Lstring("no way")).deepEquals(new Lcons(new Lstring("hello"), new Linteger(12))));
	}
	public void testSymbol() throws Exception {
		SymbolTable sym = new SymbolTable(); // force loadingof statics!
        sym.init(new NilSymbol());
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
		Interpreter interp = new Interpreter();
        Exp a = new Lsymbol("a");
		Lobject f1 = new Lobject(new StandardEnvironment(interp, new NilSymbol()));
		f1.defineVariable(a, new Lstring("foo"));
		Lobject f2 = new Lobject(new StandardEnvironment(interp, new NilSymbol()));
		f2.defineVariable(a, new Lstring("foo"));
		assertTrue(f1.deepEquals(f2));
	}
}
