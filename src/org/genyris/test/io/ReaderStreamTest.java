// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.genyris.exception.GenyrisException;
import org.genyris.io.readerstream.ReaderStream;

public class ReaderStreamTest extends TestCase {

    public void testReadFileDigest() throws Exception {
        ReaderStream rs = new ReaderStream(new InputStreamReader(
                new FileInputStream("test/fixtures/secret.txt")), "fixtures/secret.txt");
        assertEquals("5ebe2294ecd0e0f08eab7690d2a6ee69", rs.digest("MD5"));
    }
    public void testCopy() throws Exception {

	    String input = "1234567";
	    ReaderStream rs = new ReaderStream(input);
	    Writer out = new StringWriter();
	    rs.copy(out);
	    System.out.println(out);
	    assertEquals(input, out.toString());
	}
	private void testDigest(String type, String in, String expected) throws GenyrisException {
        ReaderStream rs = new ReaderStream(in);
        assertEquals(expected, rs.digest(type));
	    
	}
	public void testStringDigests() throws Exception {

	    testDigest("MD5", "", "d41d8cd98f00b204e9800998ecf8427e");
        testDigest("MD5", "\n", "68b329da9893e34099c7d8ad5cb9c940");
        testDigest("MD5", "secret", "5ebe2294ecd0e0f08eab7690d2a6ee69");

        testDigest("SHA-1", "", "da39a3ee5e6b4b0d3255bfef95601890afd80709");
        testDigest("SHA-1", "\n", "adc83b19e793491b1c6ea0fd8b46cd9f32e592fc");
        testDigest("SHA-1", "secret", "e5e9fa1ba31ecd1ae84f75caaa474f3a663f05f4");

        testDigest("SHA-256", "", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        testDigest("SHA-256", "\n", "01ba4719c80b6fe911b091a7c05124b64eeece964e09c058ef8f9805daca546b");
        testDigest("SHA-256", "secret", "2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b");
    }
}
