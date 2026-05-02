// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.readerstream

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.io.*
import org.genyris.io.writerstream.WriterStream
import java.io.IOException
import java.io.Reader
import java.io.Writer
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class ReaderStream : Atom {
    val inStream: InStream

    constructor(reader: InStream) {
        this.inStream = reader
    }

    constructor(readerEOF: InStreamEOF?) {
        this.inStream = ConvertEofInStream(readerEOF)
    }

    constructor(reader: Reader?, filename: String?) {
        this.inStream = ReaderInStream(reader, filename)
    }

    constructor(script: String) {
        this.inStream = UngettableInStream(StringInStream(script))
    }

    val reader: Reader?
        get() {
            if (this.inStream is ReaderInStream) {
                return this.inStream.getReader()
            }
            return null
        }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitExpWithEmbeddedClasses(this)
    }

    override fun toString(): String {
        return inStream.toString()
    }

    @kotlin.Throws(GenyrisException::class)
    fun close() {
        inStream.close()
    }

    abstract class AbstractReaderMethod(interp: Interpreter?, name: String?) : AbstractMethod(interp, name) {
        @kotlin.Throws(GenyrisException::class)
        protected fun getSelfReader(env: Environment): ReaderStream {
            getSelf(env)
            if (_self !is ReaderStream) {
                throw GenyrisException("Non-Reader passed to a Reader method.")
            } else {
                return _self as ReaderStream
            }
        }
    }

    class ReadMethod(interp: Interpreter?) : AbstractReaderMethod(interp, "read") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp {
            val self = getSelfReader(env)
            return Bignum(self.inStream.readNext().code)
        }
    }

    class GetLineMethod(interp: Interpreter?) : AbstractReaderMethod(interp, "getline") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            val self = getSelfReader(env)
            val buf = StringBuffer()
            if (!self.inStream.hasData()) {
                return env.getSymbolTable().EOF()
            }
            do {
                val ch: Int = self.inStream.readNext().code
                if (ch == '\r'.code) continue
                if (ch == '\n'.code) {
                    break
                } else {
                    buf.append(ch.toChar())
                }
            } while (self.inStream.hasData())
            return StrinG(buf.toString())
        }
    }

    class ReadAllMethod(interp: Interpreter?) : AbstractReaderMethod(interp, "readAll") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            val self = getSelfReader(env)
            val buf = StringBuffer()
            if (!self.inStream.hasData()) {
                return env.getSymbolTable().EOF()
            }
            do {
                val ch: Int = self.inStream.readNext().code
                buf.append(ch.toChar())
            } while (self.inStream.hasData())
            return StrinG(buf.toString())
        }
    }

    class HasDataMethod(interp: Interpreter?) : AbstractReaderMethod(interp, "hasData") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            val self = getSelfReader(env)
            return (if (self.inStream.hasData()) TRUE else NIL)
        }
    }

    class CloseMethod(interp: Interpreter?) : AbstractReaderMethod(
        interp,
        staticName
    ) {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            getSelfReader(env).close()
            return NIL
        }

        companion object {
            val staticName: String
                get() = "close"
        }
    }

    class CopyMethod(interp: Interpreter?) : AbstractReaderMethod(
        interp,
        staticName
    ) {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp? {
            val types = arrayOf<Class<*>?>(WriterStream::class.java)
            checkArgumentTypes(types, arguments)
            val output = arguments[0] as WriterStream
            val r = getSelfReader(env)
            r.copy(output.getWriter())
            return NIL
        }

        companion object {
            val staticName: String
                get() = "copy"
        }
    }

    class NewMethod(interp: Interpreter?) : AbstractReaderMethod(
        interp,
        staticName
    ) {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
            val types = arrayOf<Class<*>?>(StrinG::class.java)
            checkArgumentTypes(types, arguments)
            val theString = arguments[0] as StrinG
            val result = ReaderStream(theString.toString())
            return result
        }

        companion object {
            val staticName: String
                get() = "new"
        }
    }

    class DigestMethod(interp: Interpreter?) : AbstractReaderMethod(
        interp,
        staticName
    ) {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp {
            val types = arrayOf<Class<*>?>(StrinG::class.java)
            checkArgumentTypes(types, arguments)
            val theDigestType = arguments[0] as StrinG
            val r = getSelfReader(env)
            val md5 = r.digest(theDigestType.toString())
            return StrinG(md5)
        }

        companion object {
            val staticName: String
                get() = "digest"
        }
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.READER()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    @kotlin.jvm.JvmOverloads
    @kotlin.Throws(GenyrisException::class)
    fun copy(output: Writer, flushSize: Int = Integer.MAX_VALUE) {
        var count = 0
        while (inStream.hasData()) {
            val ch = inStream.readNext()
            try {
                output.write(ch.code)
                count += 1
                if (count > flushSize) {
                    count = 0
                    output.flush()
                }
            } catch (e: IOException) {
                throw GenyrisException(e.getMessage())
            }
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun digest(digestName: String): String {
        // 
        // Compute the digest of the input stream,
        // return the digest in Hex.
        // 
        // Digest Names: MD5, SHA-1, SHA-256
        //
        val md: MessageDigest
        try {
            md = MessageDigest.getInstance(digestName)
        } catch (e: NoSuchAlgorithmException) {
            throw GenyrisException(e.getMessage())
        }
        while (inStream.hasData()) {
            val ch = inStream.readNext()
            val charray = charArrayOf(ch)
            val temp = String(charray) // default encoding
            md.update(temp.getBytes())
        }
        val digest = md.digest()
        val sb = StringBuffer()
        for (b in digest) {
            sb.append(java.lang.String.format("%02x", b.toInt() and 0xff))
        }
        return sb.toString()
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.READER, HasDataMethod(interpreter))
            interpreter.bindMethodInstance(Constants.READER, ReadMethod(interpreter))
            interpreter.bindMethodInstance(Constants.READER, CloseMethod(interpreter))
            interpreter.bindMethodInstance(Constants.READER, GetLineMethod(interpreter))
            interpreter.bindMethodInstance(Constants.READER, CopyMethod(interpreter))
            interpreter.bindMethodInstance(Constants.READER, ReadAllMethod(interpreter))
            interpreter.bindMethodInstance(Constants.READER, NewMethod(interpreter))
            interpreter.bindMethodInstance(Constants.READER, DigestMethod(interpreter))
        }
    }
}
