package org.genyris.java;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

public class GenyrisActionListener implements ActionListener {

	private final Closure closure;
	private final Environment runtime;
    public GenyrisActionListener(Closure closure, Environment runtime) {
		this.closure = closure;
		this.runtime = runtime;
	}

	public void actionPerformed(ActionEvent e) {
    	JavaWrapper wrappedEvent = JavaUtils.wrapJavaObject(runtime, e);
    	Exp[] arguments = {wrappedEvent};
    	try {
			closure.applyFunction(runtime, arguments);
		} catch (GenyrisException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}
