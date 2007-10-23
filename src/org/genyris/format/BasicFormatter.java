package org.genyris.format;

import java.io.IOException;
import java.io.Writer;

import org.genyris.classes.BuiltinClasses;
import org.genyris.classification.ClassWrapper;
import org.genyris.core.AccessException;
import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.LazyProcedure;
import org.lispin.jlispin.interp.SpecialEnvironment;
import org.lispin.jlispin.interp.StandardEnvironment;

public class BasicFormatter implements Visitor {

	private static final String CDRCHAR = ":";
	private Writer _output;

	public BasicFormatter(Writer out) {
		_output = out;
	}

   public void visitLobject(Lobject frame) {
       if(frame.isTaggedWith(BuiltinClasses.STANDARDCLASS)) {
           new ClassWrapper(frame).acceptVisitor(this);
       }
       else {
    		try {
                _output.write(frame.getAlist().toString());

    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
       }
	}

	public void visitEagerProc(EagerProcedure proc) {
		try {
			_output.write("<EagerProc: " + proc.getJavaValue().toString() + ">");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitLazyProc(LazyProcedure proc){
		try {
			_output.write(proc.getJavaValue().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void writeCdr(Exp cons) {
		try {
			if( cons.isNil()) {
				return;
			}
			_output.write(" ");
			if( !cons.listp() ) {
				_output.write(CDRCHAR + " "); // cdr_char
				cons.acceptVisitor(this);
				return;
			}
			cons.car().acceptVisitor(this);
			if( cons.cdr().isNil()) {
				return;
			}
			writeCdr(cons.cdr());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void visitLcons(Lcons cons) {
		try {
			_output.write("(");
			cons.car().acceptVisitor(this);
            boolean colon= cons.isTaggedWith(BuiltinClasses.PRINTWITHCOLON);
            if(colon) {
                _output.write(" : ");
                cons.cdr().acceptVisitor(this);
            } else {
                writeCdr(cons.cdr());
            }
			_output.write(")");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitLdouble(Ldouble dub){
		try {
			_output.write(dub.getJavaValue().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitLinteger(Linteger lint)  {
		try {
			_output.write(lint.getJavaValue().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitBignum(Bignum bignum) {
		try {
			_output.write(bignum.getJavaValue().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitLstring(Lstring lst) {
		try {
			_output.write("\"" + lst.getJavaValue().toString() + "\"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitLsymbol(Lsymbol lsym) {
		try {
			_output.write(lsym.getJavaValue().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void visitStandardEnvironment(StandardEnvironment env) {
		try {
			_output.write( env.getJavaValue().toString() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitSpecialEnvironment(SpecialEnvironment env) {
		try {
			_output.write(env.getJavaValue().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void visitClassWrapper(ClassWrapper klass) {
        try {
            _output.write(klass.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
