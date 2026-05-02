// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.exception


class GenyrisInterruptedException(string: String?) : GenyrisException(string) {
    companion object {
        private const val serialVersionUID = 5461497754125356267L
    }
}
