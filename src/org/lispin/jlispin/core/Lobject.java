package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.lispin.jlispin.classes.BuiltinClasses;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.SpecialEnvironment;
import org.lispin.jlispin.interp.UnboundException;


public class Lobject extends Exp implements Environment {
	private Map _dict;
	private Lsymbol NIL;
	private Environment _parent;
	Exp _self, __self, _classes, _superclasses, _classname;
	
	public Lobject(Environment parent) {
		_dict = new HashMap();
		NIL = parent.getNil();
		_parent = parent;
		_self = parent.internString("self");
		__self = internString(SymbolTable.DYNAMICSCOPECHAR + "self");
		_classes = parent.internString(SymbolTable.DYNAMICSCOPECHAR + "classes");
		_superclasses = parent.internString(SymbolTable.DYNAMICSCOPECHAR + "superclasses");
		_classname = parent.internString(SymbolTable.DYNAMICSCOPECHAR + "classname");
	}
	
	public Lobject(Lsymbol key, Exp value, Lsymbol nil) {
		_dict = new HashMap();
		_dict.put(key, value);
		NIL = nil;
		_parent = null;
	}

	public int hashCode() {
    	return _dict.hashCode();
    }

	public boolean equals(Object compare) {
		if( compare.getClass() != this.getClass())
			return false;
		else 
			return _dict.equals(((Lobject)compare)._dict);
	}

	public Object getJavaValue() {
		return _dict;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitLobject(this);
	}
		
	public boolean isSelfEvaluating() {
		return false;
	}
	
	public boolean hasKey(Exp a) {
		return _dict.containsKey(a);
	}

	public Exp getAlist() {
		Iterator iter = _dict.keySet().iterator();
		Exp result = NIL;
		while(iter.hasNext()) {
			Exp key = (Exp) iter.next();
			Exp value = (Exp) _dict.get(key);
            Exp tmp = new Lcons(key, value);
            tmp.addClass(BuiltinClasses.PRINTWITHCOLON);
			result = new Lcons( tmp , result);
		}
		return result;
	}

	public void defineVariable(Exp symbol, Exp valu) {
		_dict.put(symbol, valu);		
	}

	public Exp lookupVariableValue(Exp symbol) throws UnboundException {
		if( symbol == __self ) {
			return this;
		}
		if( _dict.containsKey(symbol) ) {
			return (Exp)_dict.get(symbol);
		}
		if( _dict.containsKey(_classes) ) {
			try {
				return lookupInClasses(symbol);
			} catch (UnboundException e) {}
		}
		if(_dict.containsKey(_superclasses)) {
			return lookupInSuperClasses(symbol);				
		}
		throw new UnboundException("unbound " + symbol.toString());
	}

	private Exp lookupInClasses(Exp symbol) throws UnboundException {
		if (!_dict.containsKey(_classes)) {
			throw new UnboundException("object has no classes");
		}
		Exp classes = (Exp)_dict.get(_classes);
		while( classes != NIL) {
			try {
				Environment klass = (Environment)(classes.car());
				try {
					return (Exp)klass.lookupInThisClassAndSuperClasses(symbol);
				} catch (UnboundException e) {
					;
				} finally {
					classes = classes.cdr();
				}
			}
			catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("dict does not contain key: " + symbol.toString());
	}

	public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
		if( _dict.containsKey(symbol) ) {
			return (Exp)_dict.get(symbol);
		} else {
			return lookupInSuperClasses(symbol);
		}
	}

	private Exp lookupInSuperClasses(Exp symbol) throws UnboundException {
		if( !_dict.containsKey(_superclasses) ) {
			throw new UnboundException("object has no superclasses");
		}
		Exp superclasses = (Exp)_dict.get(_superclasses);
		while( superclasses != NIL) {
			try {
				Environment klass = (Environment)(superclasses.car());
				try {
					return (Exp)klass.lookupInThisClassAndSuperClasses(symbol);
				} catch (UnboundException e) {
					;
				} finally {
					superclasses = superclasses.cdr();
				}
			}
			catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("dict does not contain key: " + symbol.toString());
	}

	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
		if( _dict.containsKey(symbol)) {
			_dict.put(symbol, valu);
		}
		else {
			throw new UnboundException("in object, undefined variable: " + ((Lsymbol)symbol).getPrintName()); // TODO downcast
		}
	}

	public String toString() {
		return "<dict "+ getAlist().toString() +">";
	}

	public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
		if( _dict.containsKey(symbol) ) {
			return (Exp)_dict.get(symbol);
		} else { 
			throw new UnboundException("dict does not contain key: " + symbol.toString());
		}
	}

	public Exp getClasses(Lsymbol NIL) {
		if( ! _dict.containsKey(_classes) ) {
			return new Lcons(BuiltinClasses.OBJECT, NIL);
		}
		else {
			return (Exp) _dict.get(_classes) ;
		}
	}

	public void addClass(Exp klass) {
		Exp classes = NIL;
		if( _dict.containsKey(_classes) ) {
			classes = (Exp)_dict.get(_classes);
		}
		_dict.put(_classes, new Lcons (klass, classes));
	}


	public void removeClass(Exp klass) {
		Exp classes = NIL;
		if( _dict.containsKey(_classes) ) {
			classes = (Exp)_dict.get(_classes);
		}
		try {
			_dict.put(_classes, removeIf (klass, classes));
		}
		catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private Exp removeIf(Exp exp, Exp list) throws AccessException {
		if( list == NIL) {
			return NIL;
		}
		if( list == exp) {
			return removeIf(exp, list.cdr());
		} else {
			return new Lcons(list.car(), removeIf(exp, list.cdr()) );
		}
	}

	public Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException {
		Map bindings = new HashMap();
		bindings.put(_self, this);
		SpecialEnvironment newEnv = new SpecialEnvironment(environment, bindings, this); 
        if(arguments[0].listp()) {
            return Evaluator.evalSequence(newEnv, arguments[0]);
        } else {
            try {
                Lobject klass = (Lobject) Evaluator.eval(newEnv, arguments[0]);  
                this.addClass(klass);
                return this;
            }
            catch (ClassCastException e) {
                throw new LispinException("type tag failure: " + arguments[0] + " is not a class");
            }
        }
        
	}

    public boolean isTaggedWith(Lobject klass) {
        Exp classes = getClasses(NIL);
        while( classes != NIL) {
            try {
                if( classes.car() == klass)
                    return true;
            } catch (AccessException e) {
                return false;
            }
        }
        return false;
    }

    public boolean isInstanceOf(Lobject klass) {
        return isTaggedWith(klass); // TODO implent structural or nominativ subtyping here.
    }

	public Lsymbol getNil() {
		return NIL;
	}

	public Interpreter getInterpreter() {
		// TODO Auto-generated method stub
		return null;
	}

	public Exp internString(String symbolName) {
		return _parent.internString(symbolName);
	}

}
