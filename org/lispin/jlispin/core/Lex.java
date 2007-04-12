package org.lispin.jlispin.core;

public class Lex {

	private InStream _input;
	private char ch;
	
	public Lex(InStream inputSource) {
		_input = inputSource;
		ch = 0;
	}

	public Exp nextsym() {
		return null;
	}

	public static double lpower(double x, int y) {
		// Raise a number to an integer power.
		double ret = 1.0;

		if (y >= 0) {
			while (y > 0) {
				ret *= x;
				y -= 1;
			}
		} else { /* -ve power so divide */
			while (y < 0) {
				ret /= x;
				y += 1;
			}
		}
		return (ret);
	}
	
	public int asciiToInt( char x ) throws LexException
	{

	  	if( x <= '9' && x >= '0' ) {
	      return( x - '0' );
		}
	  	else {
			if( x == '.') {
	     		_input.lungetc(x);
	   	   		return( 888 ); /* This must be a float ! */
	  		}
	   		else {
				if( x == 'e' || x == 'E') {
	      			_input.lungetc(x);
	      			return( 999 ); /* This must be a mantissa ! */
	   			}
	   			else {
	     			_input.lungetc(x);
	      			return( 100 );  /* ie return value is > 9 */
	   			}
			}
		}
	}

	Exp parseNumber(int sign) throws LexException
	{
	int d;
	int leading = 0;
	int mantissa = 0;
	int mant_sign = 1;
	double trailing = 0.0;
	double place = 0.1;
	boolean itsafloat = false;


	   d = asciiToInt( ch );
	   do {
	      leading = 10*leading + d;
	      ch = _input.lgetc();
	      d = asciiToInt(ch);
	   } while( d < 10 );

	   if( d == 888) { /* parse the fractional part of a float */
			itsafloat = true;
	          ch = _input.lgetc();   /* skip the '.' */
	          ch = _input.lgetc();
	          while
	          ( (d = asciiToInt( ch )) < 10) {
	               trailing = trailing + d*place;
	               place = place/10.0;
	               ch = _input.lgetc();
	          }
	   }

	   if( d == 999) { /* parse the mantissa part of a float */
	          ch = _input.lgetc();   /* skip the 'e' */
	          ch = _input.lgetc();
	          if( ch == '-' ) {
	               mant_sign = -1;
	               ch = _input.lgetc();
	          }
	          while( (d = asciiToInt( ch )) < 10) {
	               mantissa = mantissa*10 + d;
	               ch = _input.lgetc();
	          }

	   }
	   if( itsafloat ) {
	          /* must be floating-point */
		double lp = lpower(10, mant_sign*mantissa);

	          return( new Ldouble(sign*(leading+trailing)*lp));
	   }
	   else {
	          /* must be a round integer */
	          return( new Linteger(sign*leading));
	   }
	}
	
	
}
