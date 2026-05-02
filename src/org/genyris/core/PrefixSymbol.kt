package org.genyris.core

import org.genyris.exception.GenyrisException

class PrefixSymbol(// exampes in full http://purl.org/dc/terms/creator", "http://xmlns.com/foaf/0.1/topic"
    var _prefix: String, // e.g. "http://purl.org/dc/terms/" "http://xmlns.com/foaf/0.1/"
    // e.g. "creator" "topic"
    var _localName: String, // e.g. "dc" "foaf"
    var _abbrev: String?
) : SimpleSymbol(_prefix + _localName), Comparable<Any?> {
    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitPrefixSymbol(this)
    }


    override fun getPrintName(): String {
        return _prefix + _localName
    }

    override fun hashCode(): Int {
        return _prefix.hashCode() + _localName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is PrefixSymbol) {
            return false
        }
        return _prefix == other._prefix && _localName == other._localName
    }

    override fun toString(): String {
        return this._abbrev + ":" + this._localName
    }

    val escapedPrintName: String
        get() = "|" + getPrintName() + "|"

    val abbreviatedPrintName: String
        get() = _abbrev + ":" + _localName

    fun getPrintNameOpt(expandPrefix: Boolean): String? {
        return if (expandPrefix) this.escapedPrintName else this.abbreviatedPrintName
    }
}
