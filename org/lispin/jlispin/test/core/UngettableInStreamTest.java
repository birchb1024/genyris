package org.lispin.jlispin.test.core;

import org.lispin.jlispin.core.LexException;
import org.lispin.jlispin.core.StringInStream;
import org.lispin.jlispin.core.UngettableInStream;

import junit.framework.TestCase;

public class UngettableInStreamTest extends TestCase {

	
	public void testUngettable() {
		UngettableInStream ung  = new UngettableInStream(new StringInStream("123"));
		
		try {
			ung.unGet('a');
			assertEquals('a', ung.getChar());
			assertEquals('1', ung.getChar());	
			ung.unGet('b');
			ung.unGet('c');
			ung.unGet('d');
			assertEquals('d', ung.getChar());	
			assertEquals('c', ung.getChar());	
			assertEquals('b', ung.getChar());	
			assertEquals('2', ung.getChar());	
			assertEquals('3', ung.getChar());	
		}
		catch (LexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
