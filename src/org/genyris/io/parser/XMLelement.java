package org.genyris.io.parser;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class XMLelement {
    public String uri;
    public String localName;
    public String qName;
    public Exp attributes;
    public List<XMLelement> children;
    public StringBuilder text; 
    
    public XMLelement() {
        text = new StringBuilder();
        children = new ArrayList<XMLelement>();
    }
    
    public static Exp Elem2Exp(XMLelement e, Environment env , Map<String, String> _prefixes) throws GenyrisException {
        Exp kids = env.getNil();
        Collections.reverse(e.children);
        for ( XMLelement child : e.children ) {
            kids = Pair.cons(Elem2Exp(child, env, _prefixes), kids);
        }
        String text = e.text.toString().strip();
        if ( !kids.isNil() && text.length() != 0) {
            System.err.printf("WARNING: both text and children in XML element, using children. In %s %s\n", e.qName, kids , text);
        }
        Exp body = kids;
        if (text.length() != 0) {
            body = new StrinG(text);
        }
        Exp tag = new StrinG(e.toString()); // unknown to science
        String[] sliced = e.qName.split(":");
        if  (sliced.length == 1) { // "fubar"
            env.getSymbolTable().internSymbol(new SimpleSymbol(e.qName));
        }
        else if  (sliced.length == 2) {  // "dcterms:foobar"
            String abbrev =  sliced[0];
            String name =  sliced[1];
            PrefixSymbol ps = new PrefixSymbol(e.uri, name, abbrev);
            tag = env.getSymbolTable().internSymbol(ps);
        }
        Exp result = Pair.cons2(tag, e.attributes, env.getNil());
        if (!body.isNil()) {
            result = Pair.cons3(tag, e.attributes, body, env.getNil());
        }
        return result;
    }
}
