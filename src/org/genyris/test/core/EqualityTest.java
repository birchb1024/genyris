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
import org.genyris.core.Dictionary;
import org.genyris.core.NilSymbol;
import org.genyris.core.Pair;
import org.genyris.core.PairEquals;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StrinG;
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
        assertTrue(new StrinG("hello").equals(new StrinG("hello")));
        assertFalse(new StrinG("hello1").equals(new StrinG("hello2")));
    }
    public void testCons() throws Exception {
        assertTrue(new Pair(new StrinG("hello"), new Bignum(12)).equals(new Pair(new StrinG("hello"), new Bignum(12))));
        assertFalse(new Pair(new StrinG("hello"), new StrinG("no way")).equals(new Pair(new StrinG("hello"), new Bignum(12))));
    }
    public void testPairEqual() throws Exception {
        assertTrue(new Pair(new StrinG("hello"), new Bignum(12)).equals(new PairEquals(new StrinG("hello"), new Bignum(12))));
        assertFalse(new Pair(new StrinG("hello"), new StrinG("no way")).equals(new PairEquals(new StrinG("hello"), new Bignum(12))));
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
        SimpleSymbol a = new SimpleSymbol("a");
        Dictionary f1 = new Dictionary(new StandardEnvironment(interp.getSymbolTable(), new NilSymbol()));
        f1.defineVariableRaw(a, new StrinG("foo"));
        Dictionary f2 = new Dictionary(new StandardEnvironment(interp.getSymbolTable(), new NilSymbol()));
        f2.defineVariableRaw(a, new StrinG("foo"));
        assertFalse(f1.equals(f2));
    }
}
