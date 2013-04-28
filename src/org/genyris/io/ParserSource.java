package org.genyris.io;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.PairSource;

public class ParserSource extends Parser {
    public ParserSource(Internable table, InStream stream) {
        super(table, stream, Constants.DYNAMICSCOPECHAR2, Constants.CDRCHAR,
                Constants.COMMENTCHAR);
    }

    public ParserSource(Internable table, InStream stream, char dynaChar,
            char cdrCharacter, char commentChar) {
        super(table, stream, dynaChar, cdrCharacter, commentChar);
    }
    @Override
    protected Exp cons(Exp l, Exp r, int line) {
        Exp retval = new PairSource(l, r, _lexer.getFilename(), line);
        return retval;
    }

}
