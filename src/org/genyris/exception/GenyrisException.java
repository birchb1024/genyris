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

	public GenyrisException(String message) {
		super(message);
		this._reason = null;
	}

	public GenyrisException(Exp data) {
		super(data.toString());
		this._reason = data;
	}
	

	public Exp getData() {
		return _reason != null ? _reason : new StrinG(super.getMessage());
	}

	public String getMessage() {
		return _reason != null ? _reason.toString() : super.getMessage();
	}
}
