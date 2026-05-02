// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.exception


class AccessException(string: String?) : GenyrisException(string) {
    companion object {
        private val serialVersionUID = -796986110956641426L
    }
}
