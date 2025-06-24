package org.genyris.io.parser;

import java.io.Reader;
import java.util.List;
import java.util.Stack;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

public class SAXHandler extends DefaultHandler {

    private Exp NIL;
    private Stack<Elem> _stack;
    private Elem _tree;

    public SAXHandler(Exp nil) {
        System.err.println("SAXHandler constructor");
        NIL = nil;
        _stack = new Stack<Elem>();
    }
    
    public Exp getTree() throws GenyrisException {
        System.err.println("SAXHandler getTree"/* + tree.toString()*/);
        return Elem.Elem2Exp(_tree, NIL);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Elem tag = new Elem(NIL);
        Exp tagAttributes = NIL;
        for( int index = 0 ; index < attributes.getLength(); index++ ) {
            tagAttributes = Pair.cons(
                Pair.cons(
                    new StrinG(attributes.getQName(index)), 
                    new StrinG(attributes.getValue(index))),
                tagAttributes); 
        }

        tag.uri = new StrinG(uri); 
        tag.localName = new StrinG(localName);  
        tag.qName = new StrinG(qName); 
        _stack.push(tag);
        System.err.printf("startElement %s", tag);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        
        _tree = _stack.peek();
        Elem start = _stack.pop();
        if (!_stack.empty()) {
            Elem parent = _stack.peek();
            parent.children.add(start);
            _tree = parent;
        } 

        System.err.println("endElement " + localName + " " + qName + " >" + start.text.toString().strip() + "< ");
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        if ( new String(ch, start, length).strip().length() == 0 ) {
            return;
        } 
        _stack.peek().text.append(new String(ch, start, length));
    }
    
    public void ignorableWhitespace(char[] ch, // only called when dtd present
                                int start,
                                int length)
                         throws SAXException {
    }

}
