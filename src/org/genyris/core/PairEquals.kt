package org.genyris.core


class PairEquals(car: Exp?, cdr: Exp?) : Pair(car, cdr), Comparable<Any?> {
    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.PAIREQUAL()
    }
}
