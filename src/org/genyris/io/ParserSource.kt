package org.genyris.io

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.Internable
import org.genyris.core.PairSource

class ParserSource : Parser {
    constructor(table: Internable?, stream: InStream?) : super(
        table, stream, Constants.DYNAMICSCOPECHAR2, Constants.CDRCHAR,
        Constants.COMMENTCHAR
    )

    constructor(
        table: Internable?, stream: InStream?, dynaChar: Char,
        cdrCharacter: Char, commentChar: Char
    ) : super(table, stream, dynaChar, cdrCharacter, commentChar)

    override fun cons(l: Exp?, r: Exp?, line: Int): Exp {
        val retval: Exp = PairSource(l, r, _lexer.getFilename(), line)
        return retval
    }
}
