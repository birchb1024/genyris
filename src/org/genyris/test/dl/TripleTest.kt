package org.genyris.test.dl

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.core.*
import org.genyris.dl.Triple
import org.genyris.exception.GenyrisException
import org.genyris.test.interp.TestUtilities

class TripleTest : TestCase() {
    private var interpreter: TestUtilities? = null

    @kotlin.Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        interpreter = TestUtilities()
    }

    @kotlin.Throws(Exception::class)
    private fun excerciseEval(exp: String?, expected: String?) {
        Assert.assertEquals(expected, interpreter!!.eval(exp))
    }

    private fun excerciseBadEval(exp: String?) {
        try {
            interpreter!!.eval(exp)
            fail()
        } catch (e: GenyrisException) {
        }
    }

    fun testToString() {
        Assert.assertEquals(
            Triple(SimpleSymbol(34), SimpleSymbol("s"), Bignum(99)).toString(),
            "(triple 34 s 99)"
        )
    }

    fun testTriple() {
        assertTrue(
            Triple(SimpleSymbol(34), SimpleSymbol("s"), Bignum(99))
                    !== Triple(SimpleSymbol(34), SimpleSymbol("s"), Bignum(99))
        )
        assertNotSame(
            Triple(SimpleSymbol(34), SimpleSymbol("s"), Bignum(99)),
            Triple(SimpleSymbol(34), SimpleSymbol("s"), Bignum(99))
        )
    }

    fun testTripleEquals() {
        val subject = SimpleSymbol(34)
        val predicate = SimpleSymbol("p")
        assertTrue(Triple(subject, predicate, Bignum(99)) == Triple(subject, predicate, Bignum(99)))
        assertTrue(Triple(subject, predicate, StrinG("foo")) == Triple(subject, predicate, StrinG("foo")))
        assertTrue(
            Triple(subject, predicate, Pair(StrinG("foo"), Bignum(99))) == Triple(
                subject,
                predicate,
                Pair(StrinG("foo"), Bignum(99))
            )
        )
    }

    fun testGetBuiltinClassSymbol() {
        val table = SymbolTable()
        table.init(SimpleSymbol("nil"))
        Assert.assertEquals(
            "Triple", Triple(
                SimpleSymbol(34), SimpleSymbol("s"),
                Bignum(99)
            ).getBuiltinClassSymbol(table).toString()
        )
    }

    @kotlin.Throws(Exception::class)
    fun testTripleFunction() {
        excerciseEval("(triple 1 ^s 34)", "(triple 1 s 34)")
        excerciseEval("(triple 1 ^|http://foo/| 23)", "(triple 1 |http://foo/| 23)")
        excerciseEval("((triple ^1 ^|http://foo/| 23).classes)", "(<class Triple (Builtin)>)")
        excerciseEval("(equal? (triple 1 ^S 23) (triple 1 ^S 23))", "true")
        excerciseEval("(equal? (triple ^s ^S 23) (triple ^s ^S 23))", "true")
        excerciseEval("(equal? (triple ^S ^P ^O) (triple ^S ^P ^O))", "true")

        excerciseBadEval("(triple ^(1) \"s\" 34)")
        excerciseBadEval("(equal? (triple (cons 1 2) ^P ^O) (triple (cons 1 2) ^P ^O))")
    }

    @kotlin.Throws(Exception::class)
    fun testTripleAccessorsFunction() {
        excerciseEval("((triple 1 ^s 34).subject)", "1")
        excerciseEval("((triple 1 ^s 34).predicate)", "s")
        excerciseEval("((triple 1 ^s 34).object)", "34")
    }

    @kotlin.Throws(Exception::class)
    fun testTripleAccessorsOnClassFunction() {
        excerciseEval("(defvar ^t (triple ^a ^s ^d))", "(triple a s d)")
        excerciseEval("(t.subject)", "a")
        excerciseEval("(t.predicate)", "s")
        excerciseEval("(t.object)", "d")
    }
}
