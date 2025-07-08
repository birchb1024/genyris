package org.genyris.core;

public class PrefixSymbol extends SimpleSymbol implements Comparable {
    // exampes in full http://purl.org/dc/terms/creator", "http://xmlns.com/foaf/0.1/topic"
    String _prefix; // e.g. "http://purl.org/dc/terms/" "http://xmlns.com/foaf/0.1/"
    String _abbrev; // e.g. "dc" "foaf"
    String _localName;   // e.g. "creator" "topic"
    public PrefixSymbol(String prefix, String name, String abbrev) {
        super(prefix + name);
        this._prefix = prefix;
        this._localName = name;
        this._abbrev = abbrev;
    }
    public String getPrintName() {
        return this._prefix + this._localName;
    }

    public String toString() { return this._abbrev + this._localName; };
}
