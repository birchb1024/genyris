// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.genyris.core.Constants;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

public class PrefixMapper {
    private static final String PREFIXCHAR = ":";
	private Map         _prefixes;
    private char _dynaChar;

    public PrefixMapper(char dynaChar) {
        _prefixes = new HashMap();
        _dynaChar = dynaChar;
    }


    public void addprefix(String prefix, String uri) throws GenyrisException {
    	if(prefix.equals(PREFIXCHAR)) {
    		prefix = "";
    	}
        try {
            new URL(uri);
        }
        catch (MalformedURLException e) {
            throw new GenyrisException("prefix is not mapped to a valid URL: " + uri);
        }
        if(prefix.startsWith(String.valueOf(_dynaChar))) {
            throw new GenyrisException("cannot start a prefix with " + _dynaChar + " in parse: " + prefix);
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
        return symbol.contains(PREFIXCHAR);
    }

    private static String getPrefix(String symbol) {
        return symbol.substring(0, symbol.indexOf(PREFIXCHAR));
    }

    private static String getSuffix(String symbol) {
        return symbol.substring(symbol.indexOf(PREFIXCHAR) + 1);
    }

    public SimpleSymbol symbolFactory(String news) throws GenyrisException {
        String prefix;
        if(news.equals(PREFIXCHAR) || !hasPrefix(news) ) {
            return Symbol.symbolFactory(news, false);
        }
        else {
            prefix = getPrefix(news);
            if (!_prefixes.containsKey(prefix)) {
                throw new GenyrisException("Unknown prefix: " + prefix);
            } else {
                return Symbol.symbolFactory(_prefixes.get(prefix) + getSuffix(news), false);
            }
        }
    }
    public String getCannonicalSymbol(String news) throws GenyrisException {
        String prefix;
        if(news.equals(PREFIXCHAR) || !hasPrefix(news) ) {
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
