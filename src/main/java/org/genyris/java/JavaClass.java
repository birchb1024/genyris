package org.genyris.java;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StandardClass;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;



public class JavaClass extends StandardClass {

	private Class javaClass;
	private Map staticFields; // premature optimisation
	public JavaClass(Class javaClass, Environment env) {
		super(env);
		this.javaClass = javaClass;
		staticFields = new HashMap();
		Field[] fields = javaClass.getFields();
		for (int i = 0; i < fields.length; i++) {
			if(Modifier.isStatic(fields[i].getModifiers())) {
				staticFields.put(fields[i].getName(), fields[i]);
			}			
		}
	}
	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.JAVACLASS();
	}

	public Exp dir(Internable table) {
		Iterator iter = _dict.keySet().iterator();
		Exp result = Pair.cons3(new DynamicSymbol(table.SELF()), new DynamicSymbol(table.VARS()), new DynamicSymbol(table.CLASSES()), table.NIL());
		while (iter.hasNext()) {
			Exp key = (Exp) iter.next();
			result = new Pair(new DynamicSymbol((SimpleSymbol)key), result);
		}
		Field[] fields = javaClass.getFields();
		for (int i = 0; i < fields.length; i++) {
			if(Modifier.isStatic(fields[i].getModifiers())) {
				result = new Pair(new DynamicSymbol(table.internString((fields[i].getName()))), result);
			}			
		}
		return result;
	}
	public Exp lookupDynamicVariableValue(DynamicSymbol dsymbol) throws UnboundException {
		Symbol symbol = ((DynamicSymbol)dsymbol).getRealSymbol();
		if(staticFields.containsKey(symbol.toString())) {
			Field field = (Field)staticFields.get(symbol.toString());
			try {
				return JavaUtils.javaToGenyris(_parent, field.get(null));
			} catch (IllegalArgumentException e) {
				throw new UnboundException(e.getMessage());
			} catch (IllegalAccessException e) {
				throw new UnboundException(e.getMessage());
			}
		}
		return super.lookupDynamicVariableValue(dsymbol);
	}

	public void setDynamicVariableValueRaw(Symbol symbol, Exp valu) throws UnboundException {
		if(staticFields.containsKey(symbol.toString())) {
			Field field = (Field)staticFields.get(symbol.toString());
			try {
				field.set(null, JavaUtils.convertToJava(field.getType(), valu, this));
				return;
			} catch (GenyrisException e) {
				throw new UnboundException(e.getMessage());
			} catch (IllegalArgumentException e) {
				throw new UnboundException(e.getMessage());
			} catch (IllegalAccessException e) {
				throw new UnboundException(e.getMessage());
			}
		}
		 super.setDynamicVariableValueRaw( symbol,  valu);
	}
}
