package org.lispin.jlispin.test.core;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Lsymbol;

public class LsymbolTest extends TestCase {
	
	public void testSetGet1() throws Exception {
		
		Lsymbol sym = new Lsymbol("G0");
		assertEquals("G0", sym.getPrintName());;
		
		sym = new Lsymbol("G1");
		assertEquals("G1", sym.getPrintName());

	}

}
