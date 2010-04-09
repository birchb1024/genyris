package org.genyris.java.swing;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;

public class GPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private GenyrisActionListener repaint;

	public GPanel(GenyrisActionListener closure) {
		super();
		repaint = closure;
	}

	public void paintComponent(Graphics g) {
		if (repaint != null)
			repaint.actionPerformed(new ActionEvent(g, 0, "repaint"));
	}
}
