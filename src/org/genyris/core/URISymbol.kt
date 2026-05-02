package org.genyris.core

import org.genyris.exception.GenyrisException

class URISymbol(newSym: String?) : EscapedSymbol(newSym) {
    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.URISYMBOL()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitFullyQualifiedSymbol(this)
    }
}
