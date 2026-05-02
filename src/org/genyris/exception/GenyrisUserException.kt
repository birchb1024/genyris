package org.genyris.exception

import org.genyris.core.Exp
import org.genyris.interp.Environment

class GenyrisUserException(exp: Exp, env: Environment?) : GenyrisException(exp) {
    companion object {
        private val serialVersionUID = -2590161545543312593L
    }
}
