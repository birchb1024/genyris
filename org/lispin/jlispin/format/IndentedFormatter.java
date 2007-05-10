package org.lispin.jlispin.format;

import java.io.IOException;
import java.io.Writer;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Frame;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Ldouble;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.Lstring;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.Visitor;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.LazyProcedure;

public class IndentedFormatter implements Visitor {
	
	private final int INDENT_DEPTH;
	private Writer _output;
	private int _consDepth;

	public IndentedFormatter(Writer out, int indentDepth) {
		_output = out;
		INDENT_DEPTH = indentDepth;
		_consDepth = 0;
	}
	
	private void printSpaces(int level) throws IOException {
		for( int i=1;i<level;i++)
			_output.write("   ");
	}

	public void printLcons(Lcons cons) throws IOException {
		_consDepth +=1;
		Exp head = cons;
		int countOfRight = 0;
		while ( !head.isNil()) {
			countOfRight += 1;
			if(head.listp()) {
				Lcons headCons = ((Lcons)head);
				if( headCons.car().listp() ) {
					Lcons first = ((Lcons)headCons.car());
					if(countOfRight <= INDENT_DEPTH) { 
						if(countOfRight > 1) 
							_output.write(' ');
						else
							printSpaces(_consDepth);
						_output.write(headCons.car().toString());
						head = headCons.cdr();
						continue;
					}
					else {
						_output.write('\n');;
						printSpaces(_consDepth+1);
						printLcons(first);
					}
					if( headCons.cdr().listp() ) {
						Lcons rest = (Lcons)headCons.cdr();
						if( !rest.car().listp()) {
							_output.write('\n');
							printSpaces(_consDepth+1);
							_output.write('~');
						}
					}
				}
				else {
					if(countOfRight > 1) 
						_output.write(' ');
					headCons.car().acceptVisitor(this);
				}
				head = headCons.cdr();
			}
			else {
				if(countOfRight > 1) 
					_output.write(' ');
				_output.write(". ");
				head.acceptVisitor(this);
				_consDepth -=1;
				return;
			}
		}
		_consDepth -=1;
	}
	
	
	public void visitLcons(Lcons cons) {
		try {
			printLcons(cons);
		} catch (IOException e) {
			// TODO what to do with these exceptions?
		}
	}
	public void visitFrame(Frame frame) {
		try {
			printLcons(new Lcons(SymbolTable.DICT, frame.getAlist()));
		} catch (IOException e) {
			// TODO what to do with these exceptions?
		}
	}

	public void visitEagerProc(EagerProcedure proc) {
		writeAtom(proc);
	}

	public void visitLazyProc(LazyProcedure proc){
		writeAtom(proc);
	}
	public void visitLdouble(Ldouble dub){
		writeAtom(dub);
	}

	public void visitLinteger(Linteger lint)  {
		writeAtom(lint);
	}

	private void writeAtom(Exp exp) {
		writeAtom(exp.getJavaValue().toString());
	}

	private void writeAtom(String str) {
		try {
			if( _consDepth == 0 )
				_output.write("~ ");
			_output.write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitLstring(Lstring lst) {
		writeAtom("\""+ lst.getJavaValue().toString() + "\"");
	}

	public void visitLsymbol(Lsymbol lsym) {
		writeAtom(lsym);
	}

}
