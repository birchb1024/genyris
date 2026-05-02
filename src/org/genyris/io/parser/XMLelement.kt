package org.genyris.io.parser

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.util.*

class XMLelement {
    var uri: String? = null
    var localName: String? = null
    var qName: String? = null
    var attributes: Exp? = null
    var children: MutableList<XMLelement>
    var text: StringBuilder

    init {
        text = StringBuilder()
        children = ArrayList<XMLelement>()
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun Elem2Exp(e: XMLelement, env: Environment, _prefixes: MutableMap<String?, String?>?): Exp {
            var kids: Exp = env.getNil()
            Collections.reverse(e.children)
            for (child in e.children) {
                kids = Pair.Companion.cons(Elem2Exp(child, env, _prefixes), kids)
            }
            val text: String = e.text.toString().strip()
            if (!kids.isNil() && text.length() != 0) {
                System.err.printf(
                    "WARNING: both text and children in XML element, using children. In %s %s\n",
                    e.qName,
                    kids,
                    text
                )
            }
            var body = kids
            if (text.length() != 0) {
                body = StrinG(text)
            }
            var tag: Exp? = StrinG(e.toString()) // unknown to science
            val sliced: Array<String?> = e.qName.split(":")
            if (sliced.size == 1) { // "fubar"
                env.getSymbolTable().internSymbol(SimpleSymbol(e.qName))
            } else if (sliced.size == 2) {  // "dcterms:foobar"
                val abbrev = sliced[0]
                val name = sliced[1]
                val ps = PrefixSymbol(e.uri, name, abbrev)
                tag = env.getSymbolTable().internSymbol(ps)
            }
            var result: Exp = Pair.Companion.cons2(tag, e.attributes, env.getNil())
            if (!body.isNil()) {
                result = Pair.Companion.cons3(tag, e.attributes, body, env.getNil())
            }
            return result
        }
    }
}
