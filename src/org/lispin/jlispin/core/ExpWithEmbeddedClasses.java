package org.lispin.jlispin.core;

import java.util.HashSet;
import java.util.Set;

public abstract class ExpWithEmbeddedClasses extends Exp implements Classifiable {
	private  Set _classes;

	public ExpWithEmbeddedClasses() {
		_classes = new HashSet(1);
	}
	public ExpWithEmbeddedClasses(Lobject theInbuiltClass) {
		_classes = new HashSet(1);
        if(theInbuiltClass != null)
            initClass(theInbuiltClass);
	}
	public void initClass(Lobject theInbuiltClass) {
		_classes.add(theInbuiltClass);		
	}
	public abstract Object getJavaValue();
	public abstract void acceptVisitor(Visitor guest);
		
	public void addClass(Exp klass) {
		_classes.add(klass);
	}
	public Exp getClasses(Lsymbol NIL) {
		Exp classes = NIL;
		Object arryOfObjects[] = _classes.toArray();
		for(int i=0; i< arryOfObjects.length; i++) {
			classes = new Lcons ((Exp)arryOfObjects[i], classes);
		}
		return classes;
	}
	public void removeClass(Exp klass) {
		_classes.remove(klass);		
	}
    public boolean isTaggedWith(Lobject klass) {
        return _classes.contains(klass);
    }

    public boolean isInstanceOf(Lobject klass) {
        return isTaggedWith(klass); // TODO implement structural or nominative subtyping here
    }
}
