// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.web.GenyrisHTTPD
import java.io.IOException

class SpawnHTTPDFunction(interp: Interpreter?) : TaskFunction(interp, "httpd", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        val types = arrayOf<Class<*>?>(Bignum::class.java, StrinG::class.java)
        checkArgumentTypes(types, arguments)
        val port: Int = (arguments[0] as Bignum).bigDecimalValue().intValue()
        val filename: String? = arguments[1].toString()
        val httpd1 = GenyrisHTTPD(port, filename, arguments)
        try {
            val t = httpd1.run()
            return getThreadAsDictionary(t, envForBindOperations)
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        }
    }
}
