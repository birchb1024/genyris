package org.genyris.io;

import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.interp.Debugger;

public class NullDebugger extends Debugger {
    @Override
    public void saveLocation(Exp exp, int lineNumber, Internable _table) {
    }

}
