// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io;

import junit.framework.TestCase;
import org.genyris.io.StringInStream;

public class StringInStreamTest extends TestCase {

	public void testNormal() throws Exception {

		StringInStream str1 = new StringInStream("1234567");
		assertEquals('1', str1.readNext());
		assertEquals('2', str1.readNext());
		assertEquals('3', str1.readNext());
	}

}
