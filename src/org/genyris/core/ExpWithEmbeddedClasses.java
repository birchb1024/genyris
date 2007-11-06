// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.ArrayList;
import org.genyris.classification.ClassMROComparator;
import org.genyris.interp.Environment;

public abstract class ExpWithEmbeddedClasses extends Exp implements Classifiable {
    private  ArrayList _classes;

    public ExpWithEmbeddedClasses() {
        _classes = new ArrayList(1);
    }
    public ExpWithEmbeddedClasses(Lobject theInbuiltClass) {
        _classes = new ArrayList(1);
        if(theInbuiltClass != null)
            initClass(theInbuiltClass);
    }
    public void initClass(Lobject theInbuiltClass) {
        if(_classes.contains(theInbuiltClass)) {
            return;
        }
        _classes.add(theInbuiltClass);
        sortClassesinMRO(theInbuiltClass.getParent());
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
    public Exp getClasses(Lsymbol NIL) {
        Exp classes = NIL;
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
