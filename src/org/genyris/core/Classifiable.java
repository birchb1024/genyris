package org.genyris.core;

public interface Classifiable {
	
	public Exp getClasses(Lsymbol NIL); 
	public void addClass(Exp klass);
	public void removeClass(Exp klass);
    public boolean isTaggedWith(Lobject klass);
    public boolean isInstanceOf(Lobject klass);
    

}
