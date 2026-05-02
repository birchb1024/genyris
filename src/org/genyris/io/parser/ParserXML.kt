package org.genyris.io.parser

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.Internable
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.io.InStream
import org.genyris.io.Parser
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class ParserXML(private val _symtab: Internable?, private val inputStream: InStream) : Parser(
    _symtab, inputStream, Constants.DYNAMICSCOPECHAR2, Constants.CDRCHAR,
    Constants.COMMENTCHAR
) {
    private val saxParser: SAXParser? = null
    private val _optionQname = false

    init {
        try {
            val saxParserFactory = SAXParserFactory.newInstance()
            saxParserFactory.setNamespaceAware(true)
            saxParser = saxParserFactory.newSAXParser()
        } catch (e: ParserConfigurationException) {
            throw GenyrisException(e.getMessage())
        } catch (e: SAXException) {
            throw GenyrisException(e.getMessage())
        }
    }


    @kotlin.Throws(GenyrisException::class)
    override fun read(env: Environment?): Exp? {
        val handler = SAXHandler(env)
        try {
            val `is` = InputSource(inputStream.getReader())
            saxParser!!.parse(`is`, handler)
        } catch (e: SAXException) {
            throw GenyrisException(e.getMessage())
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        }

        return handler.getTree()
    }
}
