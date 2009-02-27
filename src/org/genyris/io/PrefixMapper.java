// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import java.util.HashMap;
import java.util.Map;

import org.genyris.core.Constants;
import org.genyris.exception.GenyrisException;

public class PrefixMapper {
    private Map         _prefixes;

    public PrefixMapper() {
        _prefixes = new HashMap();
    }


    public void addprefix(String prefix, String uri) throws GenyrisException {
        if(prefix.startsWith(String.valueOf(Constants.DYNAMICSCOPECHAR2))) {
            throw new GenyrisException("cannot start a prefix with ! in parse: " + prefix);
        }
        if (_prefixes.containsKey(prefix)) {
            if(!_prefixes.get(prefix).equals(uri)) {
                throw new GenyrisException("conflicting prefix in parse: " + prefix + " " + uri);
            }
        } else {
            _prefixes.put(prefix, uri);
        }
    }

    private static boolean hasPrefix(String symbol) {
        return symbol.contains(".");
    }

    private static String getPrefix(String symbol) {
        return symbol.substring(0, symbol.indexOf("."));
    }

    private static String getSuffix(String symbol) {
        return symbol.substring(symbol.indexOf(".") + 1);
    }

    public String getCannonicalSymbol(String news) throws GenyrisException {
        String prefix;
        if(news.equals(".") || !hasPrefix(news) ) {
            return news;
        }
        else {
            prefix = getPrefix(news);
            if (!_prefixes.containsKey(prefix)) {
                throw new GenyrisException("Unknown prefix: " + prefix);
            } else {
                return _prefixes.get(prefix) + getSuffix(news);
            }
        }
    }

}
