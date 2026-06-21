package org.genyris.java.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.java.JavaUtils;

public class GenyrisActionListener implements ActionListener {

	private final Closure closure;
	private final Environment runtime;
    public GenyrisActionListener(Closure closure, Environment runtime) {
		this.closure = closure;
		this.runtime = runtime;
	}

	public void actionPerformed(ActionEvent e) {
    	Exp[] arguments = {JavaUtils.wrapJavaObject(runtime, e.getSource()), 
    			new Bignum(e.getID()), 
    			new StrinG(e.getActionCommand())};
    	try {
			closure.applyFunction(runtime, arguments);
		} catch (GenyrisException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}
