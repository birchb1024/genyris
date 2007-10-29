// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import java.io.IOException;
import java.io.Writer;

public class NullWriter extends Writer {

    public void close() throws IOException {}

    public void flush() throws IOException{ }

    public void write(char[] cbuf, int off, int len) throws IOException {}

}
