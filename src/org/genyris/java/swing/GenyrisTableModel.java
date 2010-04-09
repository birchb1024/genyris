package org.genyris.java.swing;

import javax.swing.table.AbstractTableModel;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractClosure;
import org.genyris.interp.Environment;
import org.genyris.interp.StandardEnvironment;
import org.genyris.java.JavaUtils;

public class GenyrisTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 2222008167725577587L;
	private AbstractClosure callback;

	public GenyrisTableModel(AbstractClosure callback) {
		this.callback = callback;
	}

	public int getColumnCount() {
		return callGenyrisInt("getColumnCount");
	}

	private int callGenyrisInt(String symbolName) {
		try {
			Exp[] arguments = {};
			Exp closure = runtimeEnv().lookupVariableValue(runtimeEnv().internString(symbolName));
			Exp result = closure.applyFunction(runtimeEnv(), arguments);
			return ((Bignum)result).bigDecimalValue().intValue();
		} catch (GenyrisException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private Environment runtimeEnv() {
		return new StandardEnvironment(callback.getEnv());
	}

	public int getRowCount() {
		return callGenyrisInt("getRowCount");
	}

	public Object getValueAt(int arg0, int arg1) {
		try {
			Exp[] arguments = { new Bignum(arg0), new Bignum(arg1)};
			Exp closure = runtimeEnv().lookupVariableValue(runtimeEnv().internString("getValueAt"));
			Exp result = closure.applyFunction(runtimeEnv(), arguments);
			return JavaUtils.convertToJava(Object.class, result, runtimeEnv());
		} catch (GenyrisException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			Exp[] arguments = { new Bignum(rowIndex), 
					new Bignum(columnIndex), 
					JavaUtils.javaToGenyris(runtimeEnv(), aValue)};
			Exp closure = runtimeEnv().lookupVariableValue(runtimeEnv().internString("setValueAt"));
			closure.applyFunction(runtimeEnv(), arguments);
		} catch (GenyrisException e) {
			e.printStackTrace();
		}

	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public String getColumnName(int column) {
		return Integer.toString(column);
	}
}
