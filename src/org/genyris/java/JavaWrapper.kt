// Copyright 2010 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.java

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.UnboundException
import java.lang.reflect.Field

class JavaWrapper(val value: Any) : Atom() {
    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.JAVAWRAPPER()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitJavaWrapper(this)
    }

    override fun toString(): String {
        return value.toString()
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(compare: Any?): Boolean {
        if (compare == null) return false
        if (compare.getClass() != this.getClass()) return false
        else return this.value == (compare as JavaWrapper).value
    }

    override fun compareTo(o: Any): Int {
        if (o.getClass() != this.getClass()) {
            return -1
        }
        if (o is Comparable<*> && o is Comparable<*>) {
            return (this.value as Comparable<*>).compareTo(o)
        }
        return (if (this === o) 0 else 1)
    }

    override fun eval(env: Environment?): Exp {
        return this
    }

    @kotlin.Throws(GenyrisException::class)
    override fun makeEnvironment(parent: Environment?): Environment {
        return JavaWrapperEnvironment(parent, this)
    }

    @kotlin.Throws(GenyrisException::class)
    fun setField(fieldName: String, value: Exp?, env: Environment?) {
        try {
            val field: Field = value.getClass().getField(fieldName)
            val converted = JavaUtils.convertToJava(field.getType(), value, env)
            field.setAccessible(true)
            field.set(this.value, converted)
        } catch (e: SecurityException) {
            throw UnboundException(e.getMessage())
        } catch (e: NoSuchFieldException) {
            throw UnboundException(e.getMessage())
        } catch (e: IllegalArgumentException) {
            throw UnboundException(e.getMessage())
        } catch (e: IllegalAccessException) {
            throw UnboundException(e.getMessage())
        }
    }

    override fun dir(table: Internable): Exp {
        val fields: Array<Field?> = value.getClass().getFields()
        var retval: Exp = Pair.Companion.cons4(
            DynamicSymbol(table.SELF()),
            DynamicSymbol(table.VARS()),
            DynamicSymbol(table.CLASSES()),
            DynamicSymbol(table.JAVACLASS()), table.NIL()
        )
        for (i in fields.indices.reversed()) {
            retval = Pair(DynamicSymbol(table.internString(fields[i]!!.getName())), retval)
        }
        return retval
    }

    @kotlin.Throws(UnboundException::class)
    fun getField(env: Environment, sym: Symbol): Exp? {
        try {
            val field: Field = value.getClass().getField(sym.toString())
            field.setAccessible(true)
            return JavaUtils.javaToGenyris(env, field.get(this.value))
        } catch (e: SecurityException) {
            throw UnboundException(e.getMessage())
        } catch (e: NoSuchFieldException) {
            throw UnboundException(e.getMessage())
        } catch (e: IllegalArgumentException) {
            throw UnboundException(e.getMessage())
        } catch (e: IllegalAccessException) {
            throw UnboundException(e.getMessage())
        }
    }

    fun hasField(sym: Symbol): Boolean {
        try {
            value.getClass().getField(sym.toString())
            return true
        } catch (e: SecurityException) {
            return false
        } catch (e: NoSuchFieldException) {
            return false
        }
    }
}


