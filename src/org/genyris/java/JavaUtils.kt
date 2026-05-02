package org.genyris.java

import org.genyris.core.*
import org.genyris.core.Bignum
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.io.ReaderInStream
import org.genyris.io.readerstream.ReaderStream
import java.io.Reader
import java.lang.Byte
import java.lang.Double
import java.lang.Float
import java.lang.reflect.Array
import java.lang.reflect.Modifier
import java.math.BigDecimal
import kotlin.Any
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.Char
import kotlin.Int
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.arrayOfNulls

object JavaUtils {
    fun toGenyrisName(javaClassName: String): String {
        return javaClassName.replace('.', '_').replace('[', '*')
    }

    fun javaToGenyris(env: Environment, rawResult: Any?): Exp? {
        val NIL: Exp? = env.getNil()
        if ((rawResult == null) || (rawResult is Void)) {
            return NIL
        } else if (rawResult is Boolean) {
            return if (rawResult)
                env.getSymbolTable().TRUE()
            else
                NIL
        } else if (rawResult is Char) {
            return StrinG(rawResult.toString())
        } else if (rawResult is String) {
            return StrinG(rawResult)
        } else if (rawResult is Int) { // TODO move this into a
            // factory in Bignum?
            return Bignum(rawResult)
        } else if (rawResult is Long) {
            return Bignum(rawResult)
        } else if (rawResult is Double) {
            return Bignum(rawResult)
        } else if (rawResult is Float) {
            return Bignum(rawResult)
        } else if (rawResult is Short) {
            return Bignum(rawResult)
        } else if (rawResult is Byte) {
            return Bignum(rawResult)
        } else if (rawResult is BigDecimal) {
            return Bignum(rawResult)
        } else if (rawResult.getClass().isArray()) {
            var retval = NIL
            val last = Array.getLength(rawResult) - 1
            for (i in last downTo 0) {
                retval = Pair(javaToGenyris(env, Array.get(rawResult, i)), retval)
            }
            return retval
        } else if (rawResult is Reader) {
            return ReaderStream(
                ReaderInStream(
                    rawResult,
                    rawResult.toString()
                )
            )
        }
        val result = wrapJavaObject(env, rawResult)
        return result
    }

    fun wrapJavaObject(env: Environment, javaObject: Any): JavaWrapper {
        val result = JavaWrapper(javaObject)
        try {
            val resultClass: Class<*> = javaObject.getClass()
            val klass = env.lookupVariableValue(
                env
                    .internString(toGenyrisName(resultClass.getName()))
            )
            result.addClass(klass as Dictionary?)
        } catch (e: UnboundException) {
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    fun toJavaArray(
        params: kotlin.Array<Class<*>?>,
        arguments: kotlin.Array<Exp?>,
        env: Environment
    ): kotlin.Array<Any?> {
        if (params.size == 0) return arrayOfNulls<Any>(0)
        val result = arrayOfNulls<Any>(params.size)
        if (params.size != arguments.size) {
            throw GenyrisException("toJavaArray: missmatched lengths!")
        }
        for (i in params.indices) {
            result[i] = JavaUtils.convertToJava(params[i]!!, arguments[i]!!, env)
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    fun convertToJava(klass: Class<*>, exp: Exp, env: Environment): Any? {
        var exp = exp
        if (exp is JavaWrapper) {
            return exp.getValue()
        } else if (klass == exp.getClass()) {
            return exp
        } else if (klass.isInstance(exp)) {
            return exp
        } else if (klass == Void.TYPE) {
            throw UnboundException("convertToJava: java.lang.Void.")
        } else if (klass == java.lang.Boolean.TYPE || klass == Boolean::class.java) {
            return java.lang.Boolean.valueOf(if (exp === env.getNil()) false else true)
        } else if (exp is Bignum) {
            val big = exp.bigDecimalValue()
            if (klass == Byte.TYPE || klass == kotlin.Byte::class.java) {
                return Byte.valueOf(big.byteValue())
            } else if (klass == java.lang.Short.TYPE || klass == Short::class.java) {
                return java.lang.Short.valueOf(big.shortValue())
            } else if (klass == Integer.TYPE
                || klass == Int::class.java
            ) {
                return Integer.valueOf(big.intValue())
            } else if (klass == java.lang.Long.TYPE || klass == Long::class.java) {
                return java.lang.Long.valueOf(big.longValue())
            } else if (klass == Float.TYPE || klass == kotlin.Float::class.java) {
                return Float.valueOf(big.floatValue())
            } else if (klass == Double.TYPE || klass == kotlin.Double::class.java) {
                return Double.valueOf(big.doubleValue())
            } else if (klass == String::class.java) {
                return big.toString()
            } else if (klass == Any::class.java) {
                return big as Any?
            } else if (klass == BigDecimal::class.java) {
                return big
            }
        } else if (klass == Reader::class.java && ReaderStream::class.java.isInstance(exp)) {
            return (exp as ReaderStream).getReader()
        } else if (StrinG::class.java.isInstance(exp) || Symbol::class.java.isInstance(exp)) {
            if (klass == Character.TYPE || klass == Char::class.java) {
                return Character.valueOf(exp.toString().charAt(0))
            } else return exp.toString()
        } else if (klass == (ByteArray(1)).getClass()) {
            // Byte array
            if (exp !is Pair) {
                throw UnboundException("convertToJava: was expecting a list.")
            }
            val length = exp.length(env.getNil())
            val result = ByteArray(length)
            for (i in 0..<length) {
                val item = exp.car()
                if (item !is Bignum) {
                    throw GenyrisException(
                        "Non-bignum in byte aray conversion: "
                                + item
                    )
                }
                result[i] = item.bigDecimalValue().intValue().toByte()
                exp = exp.cdr()
            }
            return result
        } else if (klass.isArray()) {
            if (exp !is Pair) {
                throw UnboundException("convertToJava: was expecting a list.")
            }
            val length = exp.length(env.getNil())
            val elementType = klass.getComponentType()
            val result = Array.newInstance(elementType, length) as kotlin.Array<Any?>
            for (i in 0..<length) {
                result[i] = convertToJava(elementType, exp.car(), env)
                exp = exp.cdr()
            }
            return result
        } else {
            throw UnboundException("unsupported conversion: " + klass + " " + exp)
        }
        throw UnboundException("unsupported conversion: " + klass + " " + exp)
    }

    @kotlin.Throws(GenyrisException::class)
    fun importJavaClass(
        interp: Interpreter, genyrisClassName: String,
        env: Environment, javaClassName: String
    ): Exp {
        var klass: Class<*>? = null
        try {
            klass = Class.forName(javaClassName)
        } catch (e: ClassNotFoundException) {
            throw GenyrisException("Java ClassNotFoundException: " + e.getMessage())
        }
        val genyrisClass = StandardClass.Companion.makeClass(
            klass,
            interp.getGlobalEnv(), interp.intern(toGenyrisName(javaClassName)),
            Pair(interp.intern(Constants.JAVA), interp.NIL)
        ) as JavaClass
        genyrisClass.addProperty(env, "java-classname", StrinG(javaClassName))
        if (genyrisClassName != javaClassName) {
            env.defineVariable(interp.intern(genyrisClassName), genyrisClass)
        }
        //
        val ctors = klass.getConstructors()
        for (c in ctors.indices) {
            val params = ctors[c].getParameterTypes()
            var name = "new"
            name += argList(params)
            val gctor = JavaCtor(interp, genyrisClass, name, ctors[c], params)
            genyrisClass.defineDynamicVariable(
                DynamicSymbol(interp.intern(name)),
                EagerProcedure(env, interp.NIL, gctor)
            )
        }
        //
        val methods = klass.getMethods()
        for (i in methods.indices) {
            val params = methods[i].getParameterTypes()
            val name = methods[i].getName() + argList(params)
            val gmethod: ApplicableFunction?
            if (Modifier.isStatic(methods[i].getModifiers())) {
                gmethod = JavaStaticMethod(interp, name, methods[i], params)
            } else {
                gmethod = JavaMethod(interp, name, methods[i], params)
            }
            val sym = interp.intern(name)
            genyrisClass.defineDynamicVariable(
                DynamicSymbol(sym),
                EagerProcedure(env, interp.NIL, gmethod)
            )
        }
        return genyrisClass
    }

    private fun argList(params: kotlin.Array<Class<*>?>): StringBuffer {
        val name = StringBuffer()
        if (params.size > 0) {
            for (p in params.indices) {
                name.append('-')
                name.append(toGenyrisName(params[p]!!.getName()))
            }
        }
        return name
    }
}
