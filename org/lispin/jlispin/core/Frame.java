package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Map;


public class Frame extends Exp {
	private Map _dict;
	
	public Frame() {
			_dict = new HashMap();
	}
	public Object getJavaValue() {
		return null;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitFrame(this);
	}
		
	public boolean isSelfEvaluating() {
		return true;
	}
	public boolean hasKey(Exp a) {
		return _dict.containsKey(a);
	}
	public void add(Exp key, Exp value) {
		_dict.put(key, value);
		
	}
}
