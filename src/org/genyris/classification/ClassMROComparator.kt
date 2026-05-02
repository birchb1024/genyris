package org.genyris.classification

import org.genyris.core.Dictionary
import org.genyris.core.Exp
import org.genyris.core.SimpleSymbol
import org.genyris.exception.AccessException
import org.genyris.interp.UnboundException
import kotlin.Any
import kotlin.Comparator
import kotlin.Int

class ClassMROComparator(private val NIL: SimpleSymbol?, private val SUPERCLASSES: SimpleSymbol?) : Comparator<Any?> {
    override fun compare(o1: Any?, o2: Any?): Int {
        val c1 = o1 as Dictionary?
        val c2 = o2 as Dictionary?
        return getClassDepth(c1) - getClassDepth(c2)
    }

    private fun getClassDepth(klass: Exp?): Int {
        val c1 = klass as Dictionary
        try {
            var superclasses = c1.lookupVariableShallow(SUPERCLASSES)
            if (superclasses === NIL) {
                return 0
            }
            var retval = 0
            while (superclasses !== NIL) {
                val tmp = 1 + getClassDepth(superclasses.car())
                if (retval < tmp) {
                    retval = tmp
                }
                superclasses = superclasses.cdr()
            }
            return retval
        } catch (e: UnboundException) {
            return 0
        } catch (e: AccessException) {
            return 1000000
        }
    }
}
