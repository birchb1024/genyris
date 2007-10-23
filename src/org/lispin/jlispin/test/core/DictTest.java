package org.lispin.jlispin.test.core;

import junit.framework.TestCase;

import org.genyris.core.Exp;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.NilSymbol;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.StandardEnvironment;

public class DictTest extends TestCase {
	
	private Lobject _frame;
	private Interpreter _interpreter;
	
	protected void setUp() throws Exception {
		super.setUp();
        _interpreter = new Interpreter();
		_frame = new Lobject(new StandardEnvironment(_interpreter, new NilSymbol()));
	}
	public void test1() throws Exception {

		Exp a = _interpreter.getSymbolTable().internString("a");
		assertEquals(false, _frame.hasKey(a));
		_frame.defineVariable(a, new Linteger(12));
		assertEquals(true, _frame.hasKey(a));
		
	}

}
