package org.lispin.jlispin.core;

import java.io.IOException;

public interface InStream {

	void unGet(char x) throws LexException;
	char lgetc();
	public boolean hasData();

}
