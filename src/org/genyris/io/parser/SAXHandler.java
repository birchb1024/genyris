package org.genyris.io.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

public class SAXHandler extends DefaultHandler {

    private Environment _env;
    private Exp NIL;
    private Stack<Elem> _stack;
    private Elem _tree;
    private Map<String, String> _prefixes;
    private boolean _optionQname;

    public SAXHandler(Environment e, boolean optionQname) {
        //System.err.println("SAXHandler constructor");
        _env = e;
        NIL = _env.getNil();
        _stack = new Stack<Elem>();
        _prefixes = new HashMap<String, String>();
        _optionQname = optionQname;
    }

    public String prefixize(String value) {
        // replace known prefixes with the shorter prefix
        for (Map.Entry<String, String> entry : _prefixes.entrySet()) {
            if (value.startsWith(entry.getValue())) {
                value = value.replace(entry.getValue(), entry.getKey() + ":");
                return value;
            }
        }
        return value;
    }

    public Exp getTree() throws GenyrisException {
        //System.err.println("SAXHandler getTree"/* + tree.toString()*/);
        Dictionary pres = new Dictionary(_env);
        for (Map.Entry<String, String> entry : _prefixes.entrySet()) {
            pres.addProperty(_env, entry.getKey(), new StrinG(entry.getValue()));
        }

        return Pair.cons(pres, Pair.cons(Elem.Elem2Exp(_tree, _env, _optionQname), NIL));
    }

    public void startPrefixMapping(String prefix, String uri) throws org.xml.sax.SAXException {
        _prefixes.put(prefix, uri);
    }

    public void endPrefixMapping(String prefix, String uri) throws org.xml.sax.SAXException {
        _prefixes.remove(prefix);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Elem tag = new Elem();
        tag.uri = uri;
        tag.localName = localName;
        tag.qName = qName;
        Exp attrs = NIL;
        for ( int i = 0; i<  attributes.getLength(); i++) {
            String canonical = attributes.getQName(i);
            if(!_optionQname) {
                canonical = attributes.getURI(i) + attributes.getLocalName(i);
            }
            attrs = Pair.cons(
                Pair.cons(_env.internString(canonical), new StrinG(prefixize(attributes.getValue(i)))),
                attrs);
        }

        tag.attributes = attrs;
        _stack.push(tag);
        if (_stack.size() == 1) {
            _tree = _stack.peek();
        }
        //System.err.printf("startElement %s", tag);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        
        _tree = _stack.peek();
        Elem start = _stack.pop();
        if (!_stack.empty()) {
            Elem parent = _stack.peek();
            parent.children.add(start);
            _tree = parent;
        } 

        //System.err.println("endElement " + localName + " " + qName + " >" + start.text.toString().strip() + "< ");
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
