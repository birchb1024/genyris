package org.genyris.core;

import org.genyris.exception.GenyrisException;

public class PrefixSymbol extends SimpleSymbol implements Comparable {
    // exampes in full http://purl.org/dc/terms/creator", "http://xmlns.com/foaf/0.1/topic"
    public String _prefix; // e.g. "http://purl.org/dc/terms/" "http://xmlns.com/foaf/0.1/"
    public String _abbrev; // e.g. "dc" "foaf"
    public String _localName;   // e.g. "creator" "topic"

    public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitPrefixSymbol(this);
	}


    public PrefixSymbol(String prefix, String name, String abbrev) {
        super(prefix + name);
        this._prefix = prefix;
        this._localName = name;
        this._abbrev = abbrev;
    }
    public String getPrintName() {
        return _prefix + _localName;
    }

    public String toString() { return this._abbrev + ":" + this._localName; };

    public String getEscapedPrintName() {
        return "|" + getPrintName() + "|";
    }

    public String getAbbreviatedPrintName() {
        return _abbrev  + ":" + _localName;
    }
    public String getPrintNameOpt(boolean expandPrefix) {
			return expandPrefix ? getEscapedPrintName() : getAbbreviatedPrintName();
    }

}
