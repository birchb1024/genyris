package org.lispin.jlispin.test.core;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Lsymbol;

public class LsymbolTest extends TestCase {
	
	public void testSetGet1() throws Exception {
		
		Lsymbol sym = new Lsymbol();
		assertEquals("G0", sym.getPrintName());;
		
		sym = new Lsymbol();
		assertEquals("G1", sym.getPrintName());

	}

}
