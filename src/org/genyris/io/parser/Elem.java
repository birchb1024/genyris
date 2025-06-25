package org.genyris.io.parser;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.xml.sax.Attributes;

import javax.management.Attribute;

public class Elem {
    public String uri;
    public String localName;
    public String qName;
    public Exp attributes;
    public List<Elem> children;
    public StringBuilder text; 
    
    public Elem() {
        text = new StringBuilder();
        children = new ArrayList<Elem>();
    }
    
    public static Exp Elem2Exp(Elem e, Environment env, boolean qName ) throws GenyrisException {
        Exp kids = env.getNil();
        Collections.reverse(e.children);
        for ( Elem child : e.children ) {
            kids = Pair.cons(Elem2Exp(child, env, qName), kids);
        }
        String text = e.text.toString().strip();
        if ( !kids.isNil() && text.length() != 0) {
            System.err.printf("WARNING: both text and children in XML element, using children. In %s %s\n", e.qName, kids , text);
        }
        if (text.length() != 0 && !kids.isNil()) {
            throw new GenyrisException(String.format("Both text and children in XML element %s%s", e.uri, e.localName ));
        }
        Exp body = kids;
        if (text.length() != 0) {
            body = new StrinG(text);
        }
        Exp tag = env.internString(e.uri + e.localName);
        if (qName) {
            tag = env.internString(e.qName);
        }
        Exp result = Pair.cons2(tag, e.attributes, env.getNil());
        if (!body.isNil()) {
            result = Pair.cons3(tag, e.attributes, body, env.getNil());
        }
        return result;
    }
}
