package org.lispin.jlispin.core;

import java.util.HashSet;
import java.util.Set;

public abstract class ExpWithEmbeddedClasses extends Exp implements Classifiable {
	private  Set _classes;

	public ExpWithEmbeddedClasses(Lobject theInbuiltClass) {
		_classes = new HashSet(1);
		_classes.add(theInbuiltClass);
	}
	public abstract Object getJavaValue();
	public abstract void acceptVisitor(Visitor guest);
		
	public void addClass(Exp klass) {
		_classes.add(klass);
	}
	public Exp getClasses() {
		Exp classes = SymbolTable.NIL;
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

}
