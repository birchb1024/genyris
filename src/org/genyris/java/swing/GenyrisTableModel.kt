package org.genyris.java.swing

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.AbstractClosure
import org.genyris.interp.Environment
import org.genyris.interp.StandardEnvironment
import org.genyris.java.JavaUtils
import javax.swing.table.AbstractTableModel

class GenyrisTableModel(private val callback: AbstractClosure) : AbstractTableModel() {
    override fun getColumnCount(): Int {
        return callGenyrisInt("getColumnCount")
    }

    private fun callGenyrisInt(symbolName: String?): Int {
        try {
            val arguments = arrayOf<Exp?>()
            val closure = runtimeEnv().lookupVariableValue(runtimeEnv().internString(symbolName))
            val result = closure.applyFunction(runtimeEnv(), arguments)
            return (result as Bignum).bigDecimalValue().intValue()
        } catch (e: GenyrisException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun runtimeEnv(): Environment {
        return StandardEnvironment(callback.getEnv())
    }

    override fun getRowCount(): Int {
        return callGenyrisInt("getRowCount")
    }

    override fun getValueAt(arg0: Int, arg1: Int): Any? {
        try {
            val arguments = arrayOf<Exp?>(Bignum(arg0), Bignum(arg1))
            val closure = runtimeEnv().lookupVariableValue(runtimeEnv().internString("getValueAt"))
            val result = closure.applyFunction(runtimeEnv(), arguments)
            return JavaUtils.convertToJava(Any::class.java, result, runtimeEnv())
        } catch (e: GenyrisException) {
            e.printStackTrace()
        }
        return null
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        try {
            val arguments = arrayOf<Exp?>(
                Bignum(rowIndex),
                Bignum(columnIndex),
                JavaUtils.javaToGenyris(runtimeEnv(), aValue)
            )
            val closure = runtimeEnv().lookupVariableValue(runtimeEnv().internString("setValueAt"))
            closure.applyFunction(runtimeEnv(), arguments)
        } catch (e: GenyrisException) {
            e.printStackTrace()
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return true
    }

    override fun getColumnName(column: Int): String {
        return Integer.toString(column)
    }

    companion object {
        private const val serialVersionUID = 2222008167725577587L
    }
}
