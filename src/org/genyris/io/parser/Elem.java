package org.genyris.io.parser;

import java.util.List;
import java.util.ArrayList;

import org.genyris.core.*;

public class Elem {
    public Exp uri;
    public Exp localName;
    public Exp qName;
    public Exp attributes;
    public List<Elem> children;
    public StringBuilder text; 
    
    public Elem(Exp NIL) {
        uri = localName = qName = attributes = NIL;
        text = new StringBuilder();
        children = new ArrayList<Elem>();
    }
    
    public static Exp Elem2Exp(Elem e, Exp NIL) {
        Exp children = NIL;
        for ( Elem child : e.children ) {
            children = Pair.cons(Elem2Exp(child, NIL), children);
        }
        Exp result = Pair.cons(
            Pair.cons4(e.uri, e.localName, e.qName, e.attributes, 
                new StrinG(e.text.toString().strip())),
                children);
        return result;
    }
}
