package org.genyris.exception;

import org.genyris.core.Exp;


public class GenyrisUserException extends GenyrisException {

    /**
    *
    */
    private static final long serialVersionUID = -2590161545543312593L;
    private Exp _reason;

    public GenyrisUserException(String msg) {
        super(msg);
        _reason = null;
    }

    public GenyrisUserException(Exp exp) {
        super("user raised exception");
        _reason = exp;
    }
    public Exp getReason() {
        return _reason;
    }
    public String getMessage() {
        return super.getMessage() + " " + _reason.toString();
    }
}
