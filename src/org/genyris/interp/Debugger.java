package org.genyris.interp;

import java.util.HashMap;
import java.util.Map;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;

public class Debugger {

    private Map<Exp, Location> _indexByExp;
    private Map<Location, Exp> _indexByFileLine;
    private String _currentFilename = "none";

    public Debugger() {
        _indexByExp = new HashMap<Exp, Location>();
        _indexByFileLine = new HashMap<Location, Exp>();
    }

    public void saveLocation(Exp exp, int lineNumber, Internable _table) {
        if (_currentFilename.equals("none")) {
            System.out.flush();
        }
        if (exp == _table.EOF() || exp.isNil() ) {
            return;
        }
        Location loc = new Location(_currentFilename, lineNumber);
        if (_indexByExp.containsKey(exp) ) { // || _indexByFileLine.containsKey(loc) ) {
            return;
        }
        _indexByExp.put(exp, loc);

        // System.out.println(_currentFilename + " " + lineNumber + exp);
    }

    class Location {
        public Location(String filename, int lineNumber) {
            this.filename = filename;
            this.lineNumber = lineNumber;
        }

        public String filename;
        public int lineNumber;
        public int hashCode() {
            return filename.hashCode() + lineNumber;
        }
        public boolean equals(Object obj) {
            return ((Location)obj).filename.equals(filename) &&
                    ((Location)obj).lineNumber == lineNumber;           
        }
    }

    public void nowParsingFile(String filename) {
        _currentFilename = filename;
    }

    public Exp lookupExpAsList(Exp body, Exp NIL) {
        Exp retval = NIL;
        if (_indexByExp.containsKey(body) ) { 
            Location loc = _indexByExp.get(body);
            retval = Pair.cons2(new StrinG(loc.filename), new Bignum(loc.lineNumber), NIL);
        }
        return retval;
    }
}
