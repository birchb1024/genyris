// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.exception.GenyrisException

class UnboundException(string: String?) : GenyrisException(string) {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = 7724821423007973204L
    }
}
