package org.genyris.java.swing;

import java.awt.Graphics;

import javax.swing.JPanel;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractClosure;
import org.genyris.java.JavaUtils;

public class GPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbstractClosure repaint;

	public GPanel(AbstractClosure closure) {
		super();
		repaint = closure;
	}

	public void paintComponent(Graphics g) {
		if (repaint != null) {
			Exp args[] = { JavaUtils.wrapJavaObject(repaint.getEnv(), g) };
			try {
				repaint.applyFunction(repaint.getEnv(), args);
			} catch (GenyrisException e) {
				e.printStackTrace();
			}
		}
	}
}
