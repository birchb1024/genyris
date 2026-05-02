package org.genyris.io.parser

import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.PrefixSymbol
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.util.*

class SAXHandler(private val _env: Environment) : DefaultHandler() {
    private val NIL: Exp?
    private val _stack: Stack<XMLelement>
    private var _tree: XMLelement? = null
    private val _prefixes: MutableMap<String?, String?>
    private val _optionQname = false

    init {
        //System.err.println("SAXHandler constructor");
        NIL = _env.getNil()
        _stack = Stack<XMLelement>()
        _prefixes = HashMap<String?, String?>()
    }

    fun prefixize(value: String): String {
        // replace known prefixes with the shorter abbreviation
        var value = value
        for (entry in _prefixes.entrySet()) {
            if (value.startsWith(entry.getValue())) {
                value = value.replace(entry.getValue(), entry.getKey() + ":")
                return value
            }
        }
        return value
    }

    @get:kotlin.Throws(GenyrisException::class)
    val tree: Exp
        get() {
            // namespces in a Alist
            var prefixes = NIL
            for (entry in _prefixes.entrySet()) {
                prefixes = Pair.Companion.cons(
                    Pair.Companion.cons(StrinG(entry.getKey()), StrinG(entry.getValue())),
                    prefixes
                )
            }

            return Pair.Companion.cons(
                prefixes,
                Pair.Companion.cons(XMLelement.Companion.Elem2Exp(_tree, _env, _prefixes), NIL)
            )
        }

    @kotlin.Throws(SAXException::class)
    override fun startPrefixMapping(prefix: String?, uri: String?) {
        _prefixes.put(prefix, uri)
    }

    @kotlin.Throws(SAXException::class)
    fun endPrefixMapping(prefix: String?, uri: String?) {
        _prefixes.remove(prefix)
    }

    @kotlin.Throws(SAXException::class)
    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes) {
        val tag = XMLelement()
        tag.uri = uri
        tag.localName = localName
        tag.qName = qName
        var attrs = NIL
        for (i in 0..<attributes.getLength()) {
            val abbrev: String? = attributes.getQName(i).split(":")[0]
            val attrname = PrefixSymbol(attributes.getURI(i), attributes.getLocalName(i), abbrev)
            attrs = Pair.Companion.cons(
                Pair.Companion.cons(
                    _env.getSymbolTable().internSymbol(attrname),
                    StrinG(prefixize(attributes.getValue(i)))
                ),
                attrs
            )
        }

        tag.attributes = attrs
        _stack.push(tag)
        if (_stack.size() == 1) {
            _tree = _stack.peek()
        }
        //System.err.printf("startElement %s", tag);
    }

    @kotlin.Throws(SAXException::class)
    override fun endElement(uri: String?, localName: String?, qName: String?) {
        _tree = _stack.peek()
        val start = _stack.pop()
        if (!_stack.empty()) {
            val parent = _stack.peek()
            parent.children.add(start)
            _tree = parent
        }


        //System.err.println("endElement " + localName + " " + qName + " >" + start.text.toString().strip() + "< ");
    }

    @kotlin.Throws(SAXException::class)
    override fun characters(ch: CharArray?, start: Int, length: Int) {
        if (String(ch, start, length).strip().length() == 0) {
            return
        }
        _stack.peek().text.append(String(ch, start, length))
    }

    @kotlin.Throws(SAXException::class)
    override fun ignorableWhitespace(
        ch: CharArray?,  // only called when dtd present
        start: Int,
        length: Int
    ) {
    }
}
