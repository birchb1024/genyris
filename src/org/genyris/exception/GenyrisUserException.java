package org.genyris.exception;

import org.genyris.core.Exp;
import org.genyris.interp.Environment;


public class GenyrisUserException extends GenyrisException {

    private static final long serialVersionUID = -2590161545543312593L;
    public GenyrisUserException(String msg) {
        super(msg);
    }

    public GenyrisUserException(Exp exp, Environment env) {
        super(exp);
    }
	public String getMessage() {
		return "GenyrisUserException: " 
		+ (_reason != null ? _reason.toString() : super.getMessage());
	}	
}
