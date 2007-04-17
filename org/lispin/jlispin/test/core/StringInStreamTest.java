package org.lispin.jlispin.test.core;

import junit.framework.TestCase;
import org.lispin.jlispin.core.*;

public class StringInStreamTest extends TestCase {
	
	public void testNormal() throws Exception {
		
		StringInStream str1 = new StringInStream("1234567");
		assertEquals('1', str1.getChar());
		assertEquals('2', str1.getChar());
		assertEquals('3', str1.getChar());		
	}

}
