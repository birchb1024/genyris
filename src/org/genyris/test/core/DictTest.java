// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.Dictionary;
import org.genyris.core.NilSymbol;
import org.genyris.core.SimpleSymbol;
import org.genyris.interp.Interpreter;
import org.genyris.interp.StandardEnvironment;

public class DictTest extends TestCase {

    private Dictionary _frame;
    private Interpreter _interpreter;

    protected void setUp() throws Exception {
        super.setUp();
        _interpreter = new Interpreter();
        _frame = new Dictionary(new StandardEnvironment(_interpreter.getSymbolTable(), new NilSymbol()));
    }
    public void test1() throws Exception {

        SimpleSymbol a = _interpreter.intern("a");
        assertEquals(false, _frame.hasKey(a));
        _frame.defineVariableRaw(a, new Bignum(12));
        assertEquals(true, _frame.hasKey(a));

    }

}
