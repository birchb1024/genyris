package org.lispin.jlispin.core;

import java.io.PrintWriter;

public class Lcons extends Exp {
	
	private  Exp _car;
	private  Exp _cdr;

	public Lcons(Exp car, Exp cdr) {
		_car = car;
		_cdr = cdr;
	}
	public Object getJavaValue() {
		Exp result[] = new Exp[2];
		result[0] = _car;
		result[1] = _cdr;
		return result;
	}
	
	public Exp car() {
		return _car;
	}

	public Exp cdr() {
		return _cdr;
	}
	
	public Exp setCar(Exp exp) throws AccessException {
		this._car = exp;;
		return this;
	}

	public Exp setCdr(Exp exp) throws AccessException {
		this._cdr = exp;;
		return this;
	}

	protected String cdrToString() {
		return " " + _car.toString() + _cdr.cdrToString();			
		
	}
	public String toString() {		
		return "(" + _car.toString() + _cdr.cdrToString() + ")";
	}
	
	public void printCdrRightAtoms(int level, PrintWriter output) {
		if( _car.listp() ) {
			output.print(' ');
			printSpaces(level, output);
			_car.printCdrRightList(level, output);
		}
		else {
			output.print(' ');
			output.print(_car.toString());
			if( _cdr != SymbolTable.NIL ) {
				output.print(' ');
				((Lcons)_cdr).printCdrRightAtoms(level, output);	
			}
		}
	}

	public void printCdrRightList(int level, PrintWriter output) {
		if( _car.listp() ) {
			_car.printCdrDown(level, output);
		}
		else {
			output.print(' ');
			output.print(_car.toString());
			if( _cdr != SymbolTable.NIL ) {
				output.print(' ');
				((Lcons)_cdr).printCdrRightList(level, output);	
			}
		}
	}
		
	public void printCdrDown(int level, PrintWriter output) {
		if( _car.listp() ) {
			output.print('\n');
			_car.printCdrRightList(level+1, output);
			if( _cdr != SymbolTable.NIL ) {
				_cdr.printCdr(level, output);
			}
		}
		else {
			output.print("\n");
			printSpaces(level+1, output);
			output.print("~");
			printCdrRight(level, output);
		}
	}

	public void print(int level, PrintWriter output) {
		printSpaces(level, output);
		output.print(_car.toString());
		if( _cdr == SymbolTable.NIL ) {
			return;
		}
		if( _cdr.listp()) {
			((Lcons)_cdr).printCdr(level, output);
		}
		else {
			output.print(" . " + _cdr.toString());
			
		}
	}


	public boolean isSelfEvaluating() {
		return false;
	}
}
