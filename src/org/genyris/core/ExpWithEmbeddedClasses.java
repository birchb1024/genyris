// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.ArrayList;
import org.genyris.classification.ClassMROComparator;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public abstract class ExpWithEmbeddedClasses extends Exp implements Classifiable {
    private  ArrayList _classes;

    public ExpWithEmbeddedClasses() {
        _classes = new ArrayList(1);
    }
    public abstract Object getJavaValue();
    public abstract void acceptVisitor(Visitor guest);

    private void sortClassesinMRO(Environment env) {
        Object[] tmp = _classes.toArray();
        java.util.Arrays.sort(tmp, new ClassMROComparator(env) );
        _classes.clear();
        for(int i=0; i< tmp.length; i++)
            _classes.add(tmp[i]); // TODO learn some Java
    }

    public void addClass(Exp k) { // TODO change signature to Lobject
        if(_classes.contains(k)) {
            return;
        }
        Lobject klass = (Lobject) k;
        _classes.add(klass);
        sortClassesinMRO(klass.getParent());
    }
    public void setClasses(Exp classList, Exp NIL) throws AccessException {
        _classes.clear();
        while(classList != NIL) {
            _classes.add(classList.car());
            classList = classList.cdr();
        }
    }
    public Exp getClasses(Environment env) throws UnboundException {
        Exp NIL = env.getNil();
        Exp buitinClassSymbol = env.internString(this.getBuiltinClassName());
        Exp buitinClass = env.lookupVariableValue(buitinClassSymbol);
        Exp classes = new Lcons (buitinClass, NIL);
        Object arryOfObjects[] = _classes.toArray();
        for(int i=0; i< arryOfObjects.length; i++) {
            classes = new Lcons ((Exp)arryOfObjects[i], classes);
        }
        return classes;
    }
    public void removeClass(Exp k) {
        Lobject klass = (Lobject) k;
        _classes.remove(klass);
        sortClassesinMRO(klass.getParent());
    }
    public boolean isTaggedWith(Lobject klass) {
        return _classes.contains(klass);
    }

}
