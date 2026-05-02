// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task

import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class ListTaskFunction(interp: Interpreter?) : TaskFunction(interp, "ps", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>?,
        envForBindOperations: Environment?
    ): Exp {
        val myself = Thread.currentThread()
        val threads = arrayOfNulls<Thread>(Thread.activeCount())
        Thread.enumerate(threads)
        var threadList: Exp = NIL
        for (i in threads.indices) {
            if (threads[i] != null && threads[i]!!.threadId() != myself.threadId()) threadList =
                Pair(getThreadAsDictionary(threads[i], envForBindOperations), threadList)
        }
        threadList = Pair(getThreadAsDictionary(myself, envForBindOperations), threadList)
        return threadList
    }
}
