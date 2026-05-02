// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.core.PrefixSymbol
import org.genyris.core.SimpleSymbol
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import java.lang.String
import java.net.URI
import java.net.URISyntaxException
import kotlin.Any
import kotlin.Boolean
import kotlin.Char
import kotlin.collections.HashMap
import kotlin.collections.MutableMap
import kotlin.plus
import kotlin.toString

class PrefixMapper(dynaChar: Char) {
    private val _abbreviations: MutableMap<*, *> // #TODOuse a Map<>
    private val _dynaChar: Char

    init {
        _abbreviations = HashMap<Any?, Any?>()
        _dynaChar = dynaChar
    }


    @kotlin.Throws(GenyrisException::class)
    fun addAbbreviation(abbrev: String, uri: String) {
        var abbrev = abbrev
        if (abbrev == ABBREVIATION_SEPARATOR_CHAR) {
            abbrev = ""
        }
        try {
            URI(uri)
        } catch (e: URISyntaxException) {
            throw GenyrisException("namespace for '" + abbrev + "' is not a valid URI: " + uri)
        }
        if (abbrev.startsWith(String.valueOf(_dynaChar))) {
            throw GenyrisException("cannot start a abbreviation with " + _dynaChar + " in parse: " + abbrev)
        }
        if (_abbreviations.containsKey(abbrev)) {
            if (_abbreviations.get(abbrev) != uri) {
                throw GenyrisException("conflicting abbreviation in parse: " + abbrev + " " + uri)
            }
        } else {
            _abbreviations.put(abbrev, uri)
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun symbolFactory(news: kotlin.String): SimpleSymbol {
        if (news == ABBREVIATION_SEPARATOR_CHAR || !hasAbbreviation(news)) {
            return Symbol.Companion.symbolFactory(news, false)
        } else {
            val abbrev: kotlin.String = getAbbreviation(news)
            val localName: kotlin.String = getLocalname(news)
            if (!_abbreviations.containsKey(abbrev)) {
                throw GenyrisException("Unknown abbreviation: " + abbrev)
            } else {
                val prefix = _abbreviations.get(abbrev) as kotlin.String?
                val pre = PrefixSymbol(prefix, localName, abbrev)
                return pre
            }
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun getCannonicalSymbol(news: kotlin.String): kotlin.String {
        val abbrev: kotlin.String?
        if (news == ABBREVIATION_SEPARATOR_CHAR || !hasAbbreviation(news)) {
            return news
        } else {
            abbrev = getAbbreviation(news)
            if (!_abbreviations.containsKey(abbrev)) {
                throw GenyrisException("Unknown prefix: " + abbrev)
            } else {
                return _abbreviations.get(abbrev).toString() + getLocalname(news)
            }
        }
    }

    companion object {
        const val ABBREVIATION_SEPARATOR_CHAR: kotlin.String = ":"
        fun hasAbbreviation(symbol: kotlin.String): Boolean {
            return symbol.contains(ABBREVIATION_SEPARATOR_CHAR)
        }

        private fun getAbbreviation(symbol: kotlin.String): kotlin.String {
            return symbol.substring(0, symbol.indexOf(ABBREVIATION_SEPARATOR_CHAR))
        }

        private fun getLocalname(symbol: kotlin.String): kotlin.String {
            return symbol.substring(symbol.indexOf(ABBREVIATION_SEPARATOR_CHAR) + 1)
        }
    }
}
