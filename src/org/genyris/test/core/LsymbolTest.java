// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;

import junit.framework.TestCase;

import org.genyris.core.SimpleSymbol;

public class LsymbolTest extends TestCase {
	
	public void testSetGet1() throws Exception {
		
		SimpleSymbol sym = new SimpleSymbol("G0");
		assertEquals("G0", sym.getPrintName());;
		
		sym = new SimpleSymbol("G1");
		assertEquals("G1", sym.getPrintName());

	}

}
