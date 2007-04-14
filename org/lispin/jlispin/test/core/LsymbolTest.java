package org.lispin.jlispin.test.core;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.Lsymbol;

import junit.framework.TestCase;

public class LsymbolTest extends TestCase {
	
	public void testSetGet1() throws Exception {
		
		Lsymbol sym = new Lsymbol();
		assertEquals("G0", sym.getPrintName());
		Exp val = new Linteger(12);
		assertEquals(val, sym.set(val));
		assertEquals(val, sym.get());
		
		sym = new Lsymbol();
		assertEquals("G1", sym.getPrintName());
		val = new Linteger(199);
		assertEquals(val, sym.set(val));
		assertEquals(val, sym.get());		
	}

}
