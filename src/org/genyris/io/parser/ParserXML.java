package org.genyris.io.parser;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.genyris.core.Constants;
import org.genyris.interp.Environment;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.PairSource;
import org.genyris.exception.GenyrisException;
import org.genyris.io.Parser;
import org.genyris.io.InStream;

public class ParserXML extends Parser {
    private SAXParser saxParser;
    private InStream inputStream;
    private Internable _symtab;
    private boolean _optionQname;

    public ParserXML(Internable table, InStream stream, boolean optionQname) throws GenyrisException {
        super(table, stream, Constants.DYNAMICSCOPECHAR2, Constants.CDRCHAR,
                Constants.COMMENTCHAR);
        inputStream = stream;
        _optionQname = optionQname;
        _symtab = table;
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            saxParser = saxParserFactory.newSAXParser();
        }
        catch (ParserConfigurationException | SAXException  e) {
            throw new GenyrisException(e.getMessage());
        }
    }


    @Override
    public Exp read(Environment env) throws GenyrisException {
        SAXHandler handler = new SAXHandler(env, _optionQname);
        try {
            saxParser.parse(new InputSource(inputStream.getReader()), handler);
        }
        catch (SAXException | IOException  e) {
            throw new GenyrisException(e.getMessage());
        }

        return handler.getTree();
    }

}
