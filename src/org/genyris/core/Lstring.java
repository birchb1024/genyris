// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.regex.PatternSyntaxException;
import org.genyris.exception.GenyrisException;
import org.genyris.core.Bignum;


public class Lstring extends ExpWithEmbeddedClasses {

    String _value;

    public Lstring(String str) {
        _value = str;
    }

    public Object getJavaValue() {
        return _value;
    }

    public String toString() {
        return _value;
    }
    public void acceptVisitor(Visitor guest)  throws GenyrisException {
        guest.visitLstring(this);
    }
    public String getBuiltinClassName() {
        return Constants.STRING;
    }

  public Exp split(Exp NIL, Lstring regex) throws GenyrisException {
    Exp result = NIL;
    try {
      String[] splitted = _value.split(regex._value);
      for(int i=splitted.length-1; i>=0; i--) {
        result = new Lcons(new Lstring(splitted[i]), result);
      }
    }
    catch(PatternSyntaxException e) {
      throw new GenyrisException(e.getMessage());
    }
    return result;
  }

  public Lstring concat(Lstring str) {
    return new Lstring(this._value.concat(str._value));
  }

  public Exp match(Symbol nil, Symbol true1, Lstring regex) {
    return (_value.matches(regex._value)? true1 : nil);
  }

  public Exp length() {
    return (new Bignum(_value.length()));
  }

public Symbol getBuiltinClassSymbol(Internable table) {
	return table.STRING();
}

}
