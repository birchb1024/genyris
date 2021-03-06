// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io;

import junit.framework.TestCase;

import org.genyris.io.LexException;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public class UngettableInStreamTest extends TestCase {


	public void testUngettable() throws LexException {
		UngettableInStream ung  = new UngettableInStream(new StringInStream("123"));

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
}
