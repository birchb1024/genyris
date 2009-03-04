// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;



import org.genyris.core.Exp;
import org.genyris.core.SymbolTable;

import junit.framework.TestCase;

public class SymbolTableTest extends TestCase {
	
	public void testSymbolTable() throws Exception {
		
		SymbolTable tab = new SymbolTable();
        tab.init(null);
		Exp foo1 = tab.internString("foo");
		Exp foo2 = tab.internString("foo");
		assertEquals(foo1, foo2);
		
		
	}

}
