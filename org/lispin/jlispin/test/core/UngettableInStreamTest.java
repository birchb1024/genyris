package org.lispin.jlispin.test.core;

import org.lispin.jlispin.io.LexException;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

import junit.framework.TestCase;

public class UngettableInStreamTest extends TestCase {

	
	public void testUngettable() {
		UngettableInStream ung  = new UngettableInStream(new StringInStream("123"));
		
		try {
			ung.unGet('a');
			assertEquals('a', ung.readNext());
			assertEquals('1', ung.readNext());	
			ung.unGet('b');
			ung.unGet('c');
			ung.unGet('d');
			assertEquals('d', ung.readNext());	
			assertEquals('c', ung.readNext());	
			assertEquals('b', ung.readNext());	
			assertEquals('2', ung.readNext());	
			assertEquals('3', ung.readNext());	
		}
		catch (LexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
