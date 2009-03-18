// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.NilSymbol;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.SymbolTable;
import org.genyris.interp.Interpreter;
import org.genyris.interp.StandardEnvironment;

public class EqualityTest extends TestCase {

    public void testInt1() throws Exception {
        assertTrue(new Bignum(12).equals(new Bignum(12)));
        assertFalse(new Bignum(11).equals(new Bignum(12)));
    }
    public void testDouble1() throws Exception {
        assertTrue(new Bignum(12.23e9).equals(new Bignum(12.23e9)));
        assertFalse(new Bignum(12.230001e9).equals(new Bignum(12.23e9)));
    }
    public void testString() throws Exception {
        assertTrue(new Lstring("hello").equals(new Lstring("hello")));
        assertFalse(new Lstring("hello1").equals(new Lstring("hello2")));
    }
    public void testCons() throws Exception {
        assertTrue(new Lcons(new Lstring("hello"), new Bignum(12)).equals(new Lcons(new Lstring("hello"), new Bignum(12))));
        assertFalse(new Lcons(new Lstring("hello"), new Lstring("no way")).equals(new Lcons(new Lstring("hello"), new Bignum(12))));
    }
    public void testSymbol() throws Exception {
        SymbolTable sym = new SymbolTable();
        sym.init(new NilSymbol());
        assertFalse(new SimpleSymbol("hello1").equals(new SimpleSymbol("hello2")));
        assertFalse(new SimpleSymbol("hello").equals(new SimpleSymbol("hello")));
        assertTrue(sym.internString("hello").equals(sym.internString("hello")));
        assertFalse(sym.internString("hello").equals(new SimpleSymbol("hello")));
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
        Exp a = new SimpleSymbol("a");
        Lobject f1 = new Lobject(new StandardEnvironment(interp.getSymbolTable(), new NilSymbol()));
        f1.defineVariableRaw(a, new Lstring("foo"));
        Lobject f2 = new Lobject(new StandardEnvironment(interp.getSymbolTable(), new NilSymbol()));
        f2.defineVariableRaw(a, new Lstring("foo"));
        assertFalse(f1.equals(f2));
    }
}
