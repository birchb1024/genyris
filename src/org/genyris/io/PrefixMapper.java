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

import org.genyris.core.PrefixSymbol;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

public class PrefixMapper {
    public static final String ABBREVIATION_SEPARATOR_CHAR = ":";
	private Map _abbreviations; // #TODOuse a Map<>
    private char _dynaChar;

    public PrefixMapper(char dynaChar) {
        _abbreviations = new HashMap();
        _dynaChar = dynaChar;
    }


    public void addAbbreviation(String abbrev, String uri) throws GenyrisException {
    	if(abbrev.equals(ABBREVIATION_SEPARATOR_CHAR)) {
    		abbrev = "";
    	}
        try {
            new URL(uri);
        }
        catch (MalformedURLException e) {
            throw new GenyrisException("namespace for '" + abbrev +"' is not a valid URL: " + uri);
        }
        if(abbrev.startsWith(String.valueOf(_dynaChar))) {
            throw new GenyrisException("cannot start a abbreviation with " + _dynaChar + " in parse: " + abbrev);
        }
        if (_abbreviations.containsKey(abbrev)) {
            if(!_abbreviations.get(abbrev).equals(uri)) {
                throw new GenyrisException("conflicting abbreviation in parse: " + abbrev + " " + uri);
            }
        } else {
            _abbreviations.put(abbrev, uri);
        }
    }

    public static boolean hasAbbreviation(String symbol) {
        return symbol.contains(ABBREVIATION_SEPARATOR_CHAR);
    }

    private static String getAbbreviation(String symbol) {
        return symbol.substring(0, symbol.indexOf(ABBREVIATION_SEPARATOR_CHAR));
    }

    private static String getLocalname(String symbol) {
        return symbol.substring(symbol.indexOf(ABBREVIATION_SEPARATOR_CHAR) + 1);
    }

    public SimpleSymbol symbolFactory(String news) throws GenyrisException {
        if(news.equals(ABBREVIATION_SEPARATOR_CHAR) || !hasAbbreviation(news) ) {
            return Symbol.symbolFactory(news, false);
        }
        else {
            String abbrev = getAbbreviation(news);
            String localName = getLocalname(news);
            if (!_abbreviations.containsKey(abbrev)) {
                throw new GenyrisException("Unknown abbreviation: " + abbrev);
            } else {
                String prefix = (String)_abbreviations.get(abbrev);
                PrefixSymbol pre =  new PrefixSymbol(prefix, localName, abbrev);
                return pre;
            }
        }
    }
    public String getCannonicalSymbol(String news) throws GenyrisException {
        String abbrev;
        if(news.equals(ABBREVIATION_SEPARATOR_CHAR) || !hasAbbreviation(news) ) {
            return news;
        }
        else {
            abbrev = getAbbreviation(news);
            if (!_abbreviations.containsKey(abbrev)) {
                throw new GenyrisException("Unknown prefix: " + abbrev);
            } else {
                return _abbreviations.get(abbrev) + getLocalname(news);
            }
        }
    }

}
