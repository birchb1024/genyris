package org.genyris.io;

import java.io.Reader;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler {

    private Exp _tree;
    private Exp NIL;
    private StringBuilder _currentInnerText;
    private Exp _currentAttributesStack;

    public SAXHandler(Exp nil) {
        System.out.println("SAXHandler constructor");
        NIL = nil;
        _tree = NIL;
        _currentAttributesStack = NIL;
        _currentInnerText = new StringBuilder();
    }
    
    public Exp getTree() throws GenyrisException {
        System.out.println("SAXHandler getTree"/* + tree.toString()*/);
        return Pair.reverse(_tree, NIL);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Exp tagAttributes = NIL;
        for( int index = 0 ; index < attributes.getLength(); index++ ) {
            tagAttributes = Pair.cons(
                Pair.cons(
                    new StrinG(attributes.getQName(index)), 
                    new StrinG(attributes.getValue(index))),
                tagAttributes); 
        }
        _currentAttributesStack = Pair.cons(tagAttributes, _currentAttributesStack);
        System.out.println("startElement " + uri + " " + localName + " " + qName + " " + attributes);
        if ( _currentInnerText.length() > 0 ) {
            System.out.println("WARN: non-leaf node with text: >" + _currentInnerText.toString().strip() + "<");
        }
        _currentInnerText = new StringBuilder();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        System.out.println("endElement " + localName + " " + qName + " >" + _currentInnerText.toString() + "< ");
        try { 
            _tree = Pair.cons(
                Pair.cons4( new StrinG(uri), 
                            new StrinG(localName),  
                            new StrinG(qName), 
                            _currentAttributesStack.car(),
                            new StrinG(_currentInnerText.toString().strip())), 
                _tree);
            _currentAttributesStack = _currentAttributesStack.cdr();
        }
        catch (Exception e) {
            throw new SAXException(e.getMessage());
        }
        _currentInnerText = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        if ( new String(ch, start, length).strip().length() == 0 ) {
            return;
        } 
        _currentInnerText.append(new String(ch, start, length));
    }
    
    public void ignorableWhitespace(char[] ch, // only called when dtd present
                                int start,
                                int length)
                         throws SAXException {
    }
}
