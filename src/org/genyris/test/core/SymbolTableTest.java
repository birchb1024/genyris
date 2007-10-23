package org.genyris.test.core;



import org.genyris.core.Exp;
import org.genyris.core.SymbolTable;

import junit.framework.TestCase;

public class SymbolTableTest extends TestCase {
	
	public void testSymbolTable() throws Exception {
		
		SymbolTable tab = new SymbolTable();
		Exp foo1 = tab.internString("foo");
		Exp foo2 = tab.internString("foo");
		assertEquals(foo1, foo2);
		
		
	}

}
