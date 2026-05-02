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

class KillTaskFunction(interp: Interpreter?) : TaskFunction(interp, "kill", true) {
    private fun getThreadById(id: Long): Thread? {
        val threads = arrayOfNulls<Thread>(Thread.activeCount())
        var retval: Thread? = null
        Thread.enumerate(threads)
        for (i in threads.indices) {
            if (threads[i]!!.threadId() == id) {
                retval = threads[i]
                break
            }
        }
        return retval
    }


    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        checkArguments(arguments, 1)
        val types = arrayOf<Class<*>?>(Bignum::class.java)
        checkArgumentTypes(types, arguments)
        val id: Long = (arguments[0] as Bignum).bigDecimalValue().longValue()
        val t = getThreadById(id)
        if (t == null) {
            return NIL
        }
        val name = t.getName()
        t.interrupt()
        return StrinG("Terminated " + id + " " + name)
    }
}
