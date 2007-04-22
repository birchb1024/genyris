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
	

	public void print(int level, PrintWriter output) {
		Exp head = this;
		while ( head != SymbolTable.NIL) {
			if(head.listp()) {
				Lcons headCons = ((Lcons)head);
				if( headCons._car.listp() ) {
					Lcons first = ((Lcons)headCons._car);
					if(this == head || this._cdr == head) { // first or second time in the loop
						printSpaces(level, output);
						output.print(headCons._car.toString() + ' ');
						head = headCons._cdr;
						continue;
					}
					else {
						output.print('\n');
						printSpaces(level+1, output);
						first.print(level +1  , output);
					}
					if( headCons._cdr.listp() ) {
						Lcons rest = (Lcons)headCons._cdr;
						if( !rest._car.listp()) {
							output.print('\n');
							printSpaces(level+1, output);
							output.print('~');
						}
					}
				}
				else {
					output.print(headCons._car.toString() + ' ');
				}
				
				head = headCons._cdr;
				
			}
			else {
				output.print(" . ");
				output.print(head.toString());
			}
		}
	}


	public boolean isSelfEvaluating() {
		return false;
	}
}
