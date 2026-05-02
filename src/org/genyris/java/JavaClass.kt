package org.genyris.java

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.UnboundException
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.collections.HashMap
import kotlin.collections.MutableIterator
import kotlin.collections.MutableMap

class JavaClass(private val javaClass: Class<*>, env: Environment?) : StandardClass(env) {
    private var staticFields: MutableMap<*, *> // premature optimisation

    init {
        staticFields = HashMap<Any?, Any?>()
        val fields = javaClass.getFields()
        for (i in fields.indices) {
            if (Modifier.isStatic(fields[i].getModifiers())) {
                staticFields.put(fields[i].getName(), fields[i])
            }
        }
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.JAVACLASS()
    }

    override fun dir(table: Internable): Exp {
        val iter: MutableIterator<*> = _dict.keySet().iterator()
        var result: Exp = Pair.Companion.cons3(
            DynamicSymbol(table.SELF()),
            DynamicSymbol(table.VARS()),
            DynamicSymbol(table.CLASSES()),
            table.NIL()
        )
        while (iter.hasNext()) {
            val key = iter.next() as Exp?
            result = Pair(DynamicSymbol(key as SimpleSymbol?), result)
        }
        val fields = javaClass.getFields()
        for (i in fields.indices) {
            if (Modifier.isStatic(fields[i].getModifiers())) {
                result = Pair(DynamicSymbol(table.internString((fields[i].getName()))), result)
            }
        }
        return result
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsymbol: DynamicSymbol): Exp? {
        val symbol = dsymbol.getRealSymbol()
        if (staticFields.containsKey(symbol.toString())) {
            val field = staticFields.get(symbol.toString()) as Field
            try {
                return JavaUtils.javaToGenyris(_parent, field.get(null))
            } catch (e: IllegalArgumentException) {
                throw UnboundException(e.getMessage())
            } catch (e: IllegalAccessException) {
                throw UnboundException(e.getMessage())
            }
        }
        return super.lookupDynamicVariableValue(dsymbol)
    }

    @kotlin.Throws(UnboundException::class)
    override fun setDynamicVariableValueRaw(symbol: Symbol, valu: Exp?) {
        if (staticFields.containsKey(symbol.toString())) {
            val field = staticFields.get(symbol.toString()) as Field
            try {
                field.set(null, JavaUtils.convertToJava(field.getType(), valu, this))
                return
            } catch (e: GenyrisException) {
                throw UnboundException(e.getMessage())
            } catch (e: IllegalArgumentException) {
                throw UnboundException(e.getMessage())
            } catch (e: IllegalAccessException) {
                throw UnboundException(e.getMessage())
            }
        }
        super.setDynamicVariableValueRaw(symbol, valu)
    }
}
