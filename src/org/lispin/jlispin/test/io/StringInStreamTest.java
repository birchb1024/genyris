package org.lispin.jlispin.test.io;

import junit.framework.TestCase;

import org.lispin.jlispin.io.StringInStream;

public class StringInStreamTest extends TestCase {
	
	public void testNormal() throws Exception {
		
		StringInStream str1 = new StringInStream("1234567");
		assertEquals('1', str1.readNext());
		assertEquals('2', str1.readNext());
		assertEquals('3', str1.readNext());		
	}

}
