package org.lispin.jlispin.core;

public interface Classifiable {
	
	public Exp getClasses(); 
	public void addClass(Exp klass);
	public void removeClass(Exp klass);
    public boolean isTaggedWith(Lobject klass);
    public boolean isInstanceOf(Lobject klass);
    

}
