package org.lispin.jlispin.format;

import java.io.IOException;
import java.io.Writer;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Visitor;

public class IndentedFormatter extends BasicFormatter implements Visitor {
	
	private final int INDENT_DEPTH;

	public IndentedFormatter(Writer out, int indentDepth) {
		super(out);
		INDENT_DEPTH = indentDepth;
	}
	
	private void printSpaces(int level) throws IOException {
		for( int i=0;i<level;i++)
			_output.write("   ");
	}

	public void printLcons(int level, Lcons cons) throws IOException {
		Exp head = cons;
		int countOfRight = 0;
		while ( !head.isNil()) {
			countOfRight += 1;
			if(head.listp()) {
				Lcons headCons = ((Lcons)head);
				if( headCons.car().listp() ) {
					Lcons first = ((Lcons)headCons.car());
					if(countOfRight <= INDENT_DEPTH) { 
						printSpaces(level);
						if(countOfRight > 1) 
							_output.write(' ');
						_output.write(headCons.car().toString());
						head = headCons.cdr();
						continue;
					}
					else {
						_output.write('\n');
						printSpaces(level+1);
						printLcons(level +1  , first);
					}
					if( headCons.cdr().listp() ) {
						Lcons rest = (Lcons)headCons.cdr();
						if( !rest.car().listp()) {
							_output.write('\n');
							printSpaces(level+1);
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
				return;
			}
		}
	}

	
	
	public void visitLcons(Lcons cons) {
		try {
			printLcons(0, cons);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
