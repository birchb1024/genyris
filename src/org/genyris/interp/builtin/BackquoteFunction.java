// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class BackquoteFunction extends ApplicableFunction {
	 
    private Exp COMMA, COMMA_AT;
    public BackquoteFunction(Interpreter interp) {
    	super(interp, Constants.TEMPLATE, false);
        COMMA = interp.getSymbolTable().COMMA();
        COMMA_AT = interp.getSymbolTable().COMMA_AT();
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException {
		checkArguments(arguments, 1);
        return backQuoteAux(envForBindOperations, arguments[0]);
    }

    private Exp backQuoteAux(Environment env, Exp sexp) throws GenyrisException {
        if(sexp == NIL || (!sexp.listp())) {
            return sexp;
        }
        else {
            Pair list = (Pair) sexp;
            if( list.car() == COMMA) {
                return list.cdr().car().eval(env);
            }
            else if(list.car().listp() && list.car().car() == COMMA_AT) {
                Exp res = list.car().cdr().car().eval(env);
                Exp rest = backQuoteAux(env, list.cdr() );
                return append(res, rest);
            }
            else {
                return new Pair(backQuoteAux(env, list.car()) , backQuoteAux(env, list.cdr()) );
            }
        }
    }
    private Exp append(Exp l1, Exp l2) throws AccessException {
        if( l1 == NIL) {
            return l2;
        }
        else {
            return new Pair( l1.car(), append(l1.cdr(), l2));
        }
    }
}
/*
 * (df backquote (_s)
 *  (_bq1 _s) )
 *
 *
 *(defun _bq1 (_s1)
 *  (cond
 *      ((or (null _s1) (atom _s1)) _s1)
 *      ((equal (car _s1) 'comma) (eval (cadr _s1)))
 *      ((and (not (atom (car _s1))) (equal (caar _s1) 'comma-at))
 *          (append (eval (cadar _s1)) (_bq1 (cdr _s1))))
 *
 *      (t (cons (_bq1 (car _s1)) (_bq1 (cdr _s1)))) ) )
 *
 */

//EXP backquote_fn( EXP s)
//{
//    lif(lor(null(s), atom(s))){
//        return(s);
//    }
//    else lif(equal(car(s), comma))  {
//        return(eval(car(cdr(s))));
//    }
//    else lif(land(lnot(atom(car(s))),equal(car(car(s)),comma_at))) {
//        EXP hold = reference(car(cdr(car(s))));
//        EXP tmp = reference(eval(hold));
//        EXP result = reference(append(tmp, backquote_fn(cdr(s))));
//        purge(tmp);
//        dereference(result); dereference(hold);
//        return(result);
//    }
//    else {
//        return(cons(backquote_fn(car(s)),backquote_fn(cdr(s))));
//    }
//}
