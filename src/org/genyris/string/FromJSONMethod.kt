// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

class FromJSONMethod(interp: Interpreter?) : AbstractStringMethod(
    interp,
    staticName
) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp? {
        val types = arrayOf<Class<*>?>()
        checkArgumentTypes(types, arguments)
        val theString = getSelfString(env).toString()
        try {
            val lexer = JSONTokener(theString)
            val ch = lexer.nextClean()
            if (ch == '{') {
                val jo = JSONObject(theString)
                return convertJSON(jo, env)
            } else if (ch == '[') {
                val jo = JSONArray(theString)
                return convertJSON(jo, env)
            } else {
                throw GenyrisException("Expexting non-atomic JSON - got " + theString)
            }
        } catch (e: JSONException) {
            throw GenyrisException(e.getMessage())
        }
    }


    @kotlin.Throws(GenyrisException::class)
    fun convertJSON(obj: Any, env: Environment): Exp? {
        try {
            if (obj is Int) {
                return Bignum(obj)
            } else if (obj is Long) {
                return Bignum(obj)
            } else if (obj is Float) {
                return Bignum(obj)
            } else if (obj is Double) {
                return Bignum(obj)
            } else if (obj is String) {
                return StrinG(obj)
            } else if (obj is Boolean) {
                return if (obj) TRUE else NIL
            } else if (obj === JSONObject.NULL) {
                return NIL
            } else if (obj is JSONObject) {
                val jo = obj
                if (jo.isEmpty()) {
                    return NIL
                }
                val dict = Dictionary(env)
                val keys: MutableIterator<*> = jo.keys()
                while (keys.hasNext()) {
                    val key = keys.next() as String?
                    val key_sym = DynamicSymbol(env.getSymbolTable().internString(key))
                    val value = jo.get(key)
                    dict.defineDynamicVariable(key_sym, convertJSON(value, env))
                }
                return dict
            } else if (obj is JSONArray) {
                var head: Exp? = NIL
                val ja = obj
                for (index in ja.length() - 1 downTo 0) {
                    head = Pair.Companion.cons(convertJSON(ja.get(index), env), head)
                }
                return head
            } else {
                throw GenyrisException("Unknown JSON type" + obj.toString())
            }
        } catch (e: JSONException) {
            throw GenyrisException(e.getMessage())
        }
    }

    companion object {
        val staticName: String
            get() = Constants.FROMJSON
    }
}
