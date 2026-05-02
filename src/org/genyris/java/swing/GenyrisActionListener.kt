package org.genyris.java.swing

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.java.JavaUtils
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class GenyrisActionListener(private val closure: Closure, private val runtime: Environment) : ActionListener {
    override fun actionPerformed(e: ActionEvent) {
        val arguments = arrayOf<Exp?>(
            JavaUtils.wrapJavaObject(runtime, e.getSource()),
            Bignum(e.getID()),
            StrinG(e.getActionCommand())
        )
        try {
            closure.applyFunction(runtime, arguments)
        } catch (e1: GenyrisException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }
    }
}
