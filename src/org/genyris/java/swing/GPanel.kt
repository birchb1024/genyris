package org.genyris.java.swing

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.AbstractClosure
import org.genyris.java.JavaUtils
import java.awt.Graphics
import javax.swing.JPanel

class GPanel(private val repaint: AbstractClosure?) : JPanel() {
    public override fun paintComponent(g: Graphics?) {
        if (repaint != null) {
            val args: Array<Exp?>? = arrayOf<Exp?>(JavaUtils.wrapJavaObject(repaint.getEnv(), g))
            try {
                repaint.applyFunction(repaint.getEnv(), args)
            } catch (e: GenyrisException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
