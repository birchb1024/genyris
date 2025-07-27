// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.exception;

import org.genyris.core.Exp;
import org.genyris.core.StrinG;

public class GenyrisException extends Exception {

	private static final long serialVersionUID = 2930499792506317096L;
	protected Exp _reason;
	public String filename;
	public int lineNumber;

	public GenyrisException(Exception e) {
		super(e);
	    String message = e.getMessage();
	    if( message == null) {
	        message = e.toString();
	    }
		this._reason = new StrinG(message);
	}
	
	public GenyrisException(String message) {
		super(message);
		this._reason = new StrinG(message);
	}

	public GenyrisException(Exception e, String  filename, int lineNumber) {
		super(e);
	    String message = e.getMessage();
	    if( message == null) {
	        message = e.toString();
	    }
		this._reason = new StrinG(message);
		this.filename = filename;
		this.lineNumber = lineNumber;
	}

	public GenyrisException(Exp data) {
		super(data.toString());
		this._reason = data;
	}

	public String getMessage() {
		String tmp = "";
		if (this.filename != null) {
			tmp +=  this.filename + ":" + Integer.toString(this.lineNumber) + " ";
		}
		if (_reason != null) {
			tmp += super.getMessage();
		}
		return tmp;
	}

	public Exp getData() {
		return _reason != null ? _reason : new StrinG("null");
	}
}
