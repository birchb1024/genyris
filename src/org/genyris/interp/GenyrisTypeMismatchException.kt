package org.genyris.interp

import org.genyris.exception.GenyrisException

class GenyrisTypeMismatchException(string: String?) : GenyrisException(string) {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = 189724077047918329L
    }
}
