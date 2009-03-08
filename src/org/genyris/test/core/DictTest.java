// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;

import junit.framework.TestCase;

import org.genyris.core.Exp;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.NilSymbol;
import org.genyris.interp.Interpreter;
import org.genyris.interp.StandardEnvironment;

public class DictTest extends TestCase {

    private Lobject _frame;
    private Interpreter _interpreter;

    protected void setUp() throws Exception {
        super.setUp();
        _interpreter = new Interpreter();
        _frame = new Lobject(new StandardEnvironment(_interpreter.getSymbolTable(), new NilSymbol()));
    }
    public void test1() throws Exception {

        Exp a = _interpreter.intern("a");
        assertEquals(false, _frame.hasKey(a));
        _frame.defineVariableRaw(a, new Linteger(12));
        assertEquals(true, _frame.hasKey(a));

    }

}
