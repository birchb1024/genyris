package org.genyris.test.dl

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.core.*
import org.genyris.dl.AbstractGraph
import org.genyris.dl.GraphHashSimple
import org.genyris.dl.Triple
import org.genyris.exception.GenyrisException
import org.genyris.test.interp.TestUtilities

class GraphHashSimpleTest : TestCase() {
    private var interpreter: TestUtilities? = null

    @kotlin.Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        interpreter = TestUtilities()
    }

    @kotlin.Throws(Exception::class)
    private fun AssertInterpretEquals(input: String?, expectedOutput: String?) {
        Assert.assertEquals(expectedOutput, interpreter!!.eval(input))
    }

    private fun BadAssertInterpretEquals(exp: String?) {
        try {
            interpreter!!.eval(exp)
            fail()
        } catch (e: GenyrisException) {
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun testBasicGraph1() {
        val ts = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)
        val ts2: AbstractGraph = GraphHashSimple()
        Assert.assertEquals(ts2.empty(), true)
        assertTrue(ts == ts2)

        val subject: Symbol = SimpleSymbol("1".toString())
        val predicate = SimpleSymbol("s")
        val `object`: Exp = StrinG("$")
        ts.add(Triple(subject, predicate, `object`))
        assertTrue(ts.contains(subject, predicate, `object`) != null)

        ts2.add(Triple(SimpleSymbol("1"), SimpleSymbol("s"), StrinG("$")))
        assertTrue(ts == ts2)

        val result = ts.select(subject, null, null, null, null)
        assertTrue(ts == result)
    }

    fun testBasicGraph2() {
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val S1: Symbol = SimpleSymbol("12")
        val P1 = SimpleSymbol("s")
        val O1 = StrinG("$")

        ts.add(Triple(S1, P1, O1))
        Assert.assertEquals(ts.length(), 1)
        Assert.assertEquals(ts.empty(), false)

        ts.add(Triple(S1, P1, O1))
        Assert.assertEquals(1, ts.length())
        Assert.assertEquals(false, ts.empty())

        val P2: Symbol = SimpleSymbol("s")
        val O2: Exp = StrinG("$")
        ts.add(Triple(S1, P2, O2))
        Assert.assertEquals(1, ts.length())
        Assert.assertEquals(false, ts.empty())

        ts.remove(Triple(S1, P1, O1))
        Assert.assertEquals(0, ts.length())
        Assert.assertEquals(true, ts.empty())

        ts.remove(Triple(S1, P2, O2))
        Assert.assertEquals(0, ts.length())
        Assert.assertEquals(true, ts.empty())
    }

    fun testGetOK() {
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val S1 = SimpleSymbol("12")
        val P1 = SimpleSymbol("s")
        val O1 = StrinG("$")
        ts.add(Triple(S1, P1, O1))
        ts.add(Triple(S1, SimpleSymbol("s"), StrinG("$")))

        Assert.assertEquals(ts.empty(), false)

        try {
            assertEquals(ts.get(S1, P1), O1)
        } catch (e: GenyrisException) {
            fail()
        }

        Assert.assertEquals(ts.empty(), false)
    }

    fun testGetNone() {
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val subject = SimpleSymbol(12)
        val predicate = SimpleSymbol("s")
        try {
            ts.get(subject, predicate)
            fail()
        } catch (e: GenyrisException) {
        }
    }

    fun testGetTooMany() {
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val subject = SimpleSymbol(12)
        val predicate = SimpleSymbol("s")
        val `object` = StrinG("$1")
        ts.add(Triple(subject, predicate, `object`))
        ts.add(Triple(subject, predicate, StrinG("$2")))
        ts.add(Triple(subject, SimpleSymbol("s"), StrinG("$")))

        Assert.assertEquals(ts.empty(), false)

        try {
            ts.get(subject, predicate)
            fail()
        } catch (e: GenyrisException) {
        }

        Assert.assertEquals(ts.empty(), false)
    }

    fun testGetListOne() {
        val NIL: Exp = NilSymbol()
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val subject: Symbol = SimpleSymbol("12")
        val predicate = SimpleSymbol("s")
        val `object` = StrinG("$")
        ts.add(Triple(subject, predicate, `object`))
        assertEquals(ts.getList(subject, predicate, NIL), Pair(`object`, NIL))
    }

    fun testGetListNone() {
        val NIL: Exp = NilSymbol()
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val subject = SimpleSymbol(12)
        val predicate = SimpleSymbol("s")
        val `object` = StrinG("$")
        ts.add(Triple(subject, predicate, `object`))
        assertEquals(ts.getList(SimpleSymbol("s2"), predicate, NIL), NIL)
    }

    fun testGetListMany() {
        val NIL: Exp = NilSymbol()
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val subject: Symbol = SimpleSymbol("12")
        val predicate = SimpleSymbol("s")
        val object1 = StrinG("$1")
        val object2 = StrinG("$2")
        ts.add(Triple(subject, predicate, object1))
        ts.add(Triple(subject, predicate, object2))
        if (ts.getList(subject, predicate, NIL) == Pair(object1, Pair(object2, NIL))
            || ts.getList(subject, predicate, NIL) == Pair(object2, Pair(object1, NIL))
        ) {
        } else {
            fail()
        }
    }

    fun testPutOK() {
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val subject = SimpleSymbol(12)
        val predicate = SimpleSymbol("s")
        val object1 = StrinG("$1")
        val object2 = StrinG("$2")

        ts.add(Triple(subject, predicate, object1))
        ts.add(Triple(subject, predicate, object1))
        ts.add(Triple(subject, predicate, object2))
        ts.add(Triple(subject, predicate, object2))
        ts.put(subject, predicate, object2)
        ts.put(subject, predicate, object2)
        try {
            assertEquals(ts.get(subject, predicate), object2)
            Assert.assertEquals(ts.length(), 1)
        } catch (e: GenyrisException) {
            fail()
        }
    }

    fun testPutWasEmpty() {
        val ts: AbstractGraph = GraphHashSimple()
        assertEquals(ts, ts)
        Assert.assertEquals(ts.empty(), true)

        val subject = SimpleSymbol(12)
        val predicate = SimpleSymbol("s")
        val `object` = StrinG("$2")

        ts.put(subject, predicate, `object`)
        try {
            assertEquals(ts.get(subject, predicate), `object`)
            Assert.assertEquals(ts.length(), 1)
        } catch (e: GenyrisException) {
            fail()
        }
    }

    @kotlin.Throws(Exception::class)
    fun testInterpAdd() {
        AssertInterpretEquals("(defvar ^g (graph))", "(graph)")
        AssertInterpretEquals("(g (.add ^z ^x 42 ))", "(graph (triple z x 42))")
        AssertInterpretEquals("(g (.add ^z ^x 43 ))", "(graph (triple z x 42) (triple z x 43))")
        AssertInterpretEquals("(sort (g (.asTriples )))", "((triple z x 42) (triple z x 43))")

        AssertInterpretEquals("(defvar ^g (graph))", "(graph)")
        AssertInterpretEquals(
            "(g (.add ^s `((a = $(+ 2 5)) (b = $(+ 5 8))) ))",
            "(graph (triple s a 7) (triple s b 13))"
        )
        AssertInterpretEquals("(sort (g (.asTriples )))", "((triple s a 7) (triple s b 13))")

        AssertInterpretEquals("(defvar ^g (graph))", "(graph)")
        AssertInterpretEquals("(g (.add ^s (data (a = 'A') (b = 'B')) ))", "(graph (triple s a 'A') (triple s b 'B'))")
        AssertInterpretEquals("(sort (g (.asTriples )))", "((triple s a 'A') (triple s b 'B'))")

        AssertInterpretEquals("(defvar ^g (graph))", "(graph)")
        AssertInterpretEquals("(g (.add ^x (data (a = 'A') (a = 'B')) ))", "(graph (triple x a 'A') (triple x a 'B'))")
        AssertInterpretEquals("(sort (g (.asTriples)))", "((triple x a 'A') (triple x a 'B'))")
    }

    @kotlin.Throws(Exception::class)
    fun testFormatting() {
        AssertInterpretEquals("(triple ^a ^b 'west')", "(triple a b 'west')")
        AssertInterpretEquals("(triple 'X' ^b 'west')", "(triple X b 'west')")
        AssertInterpretEquals("(triple 'X' ^b 123)", "(triple X b 123)")
        BadAssertInterpretEquals("(triple ^(2=3) ^b 123)")
        BadAssertInterpretEquals("(triple ^(2=3) ^b (dict (.z =99)))")
        BadAssertInterpretEquals("(triple ^(2=3) ^b (list 1 2 3 4 5))")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpStore() {
        AssertInterpretEquals(
            "(null? (member? ^.asTriples ((car ((graph).classes)).vars)))", "nil"
        )
        AssertInterpretEquals("(defvar ^ts (graph))", "(graph)")
        AssertInterpretEquals("(ts.classes)", "(<class Graph (Builtin)>)")
        AssertInterpretEquals("(ts(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts(.add (triple ^s 3 ^o)))", "(graph (triple s 3 o) (triple s p o))")
        AssertInterpretEquals("(ts(.asTriples))", "((triple s 3 o) (triple s p o))")

        AssertInterpretEquals("(ts(.select ^s nil nil)))", "(graph (triple s 3 o) (triple s p o))")
        AssertInterpretEquals("(equal? ts (ts(.select nil nil nil)))", "true")
        AssertInterpretEquals("(equal? 2 ((ts(.select ^s nil nil))(.length)))", "true")
        AssertInterpretEquals("(equal? 1 ((ts(.select ^s ^p nil))(.length)))", "true")
        AssertInterpretEquals("(equal? ts (ts(.select ^s ^p ^o)))", "nil")
        AssertInterpretEquals("(equal? ts (ts(.select ^X ^p ^o)))", "nil")
        AssertInterpretEquals("(equal? ts (ts(.select ^s ^X ^o)))", "nil")
        AssertInterpretEquals("(equal? ts (ts(.select ^s ^p ^X)))", "nil")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpStoreMulti() {
        AssertInterpretEquals(
            "(null? (member? ^.asTriples ((car ((graph).classes)).vars)))",
            "nil"
        )
        AssertInterpretEquals("(defvar ^ts (graph))", "(graph)")
        AssertInterpretEquals("(ts.classes)", "(<class Graph (Builtin)>)")
        AssertInterpretEquals("(ts(.add (triple ^s ^p ^o1)))", "(graph (triple s p o1))")
        AssertInterpretEquals("(ts(.add (triple ^s ^p ^o2)))", "(graph (triple s p o1) (triple s p o2))")
        AssertInterpretEquals("(ts(.add (triple ^x ^p ^z)))", "(graph (triple s p o1) (triple s p o2) (triple x p z))")
        BadAssertInterpretEquals("(ts(.add (triple ^s ^(1 = 2) ^o)))")
        AssertInterpretEquals("(ts(.select ^s nil nil)))", "(graph (triple s p o1) (triple s p o2))")
        AssertInterpretEquals("(equal? ts (ts(.select nil nil nil)))", "true")
        AssertInterpretEquals("(equal? ts (ts(.select ^s nil nil)))", "nil")
        AssertInterpretEquals("(equal? ts (ts(.select ^s ^p nil)))", "nil")
        AssertInterpretEquals("(equal? ts (ts(.select ^s ^p ^o1)))", "nil")
        AssertInterpretEquals("(equal? ts (ts(.select ^X ^p ^o)))", "nil")
        AssertInterpretEquals("(equal? ts (ts(.select ^s ^X ^o)))", "nil")
        AssertInterpretEquals("(equal? ts (ts(.select ^s ^p ^X)))", "nil")

        AssertInterpretEquals(
            "((ts(.select ^s nil nil))(.asTriples))",
            "((triple s p o2) (triple s p o1))"
        )
        AssertInterpretEquals(
            "((ts(.select ^s ^p nil))(.asTriples))",
            "((triple s p o2) (triple s p o1))"
        )
        AssertInterpretEquals("((ts(.select ^s ^p ^o1))(.asTriples))", "((triple s p o1))")
        AssertInterpretEquals("((ts(.select ^s ^p ^o2))(.asTriples))", "((triple s p o2))")

        AssertInterpretEquals("(sort (ts(.asTriples)))", "((triple s p o1) (triple s p o2) (triple x p z))")
        AssertInterpretEquals("((ts(.select nil ^p ^o2))(.asTriples))", "((triple s p o2))")

        AssertInterpretEquals("((ts(.select nil nil ^o2))(.asTriples))", "((triple s p o2))")
        AssertInterpretEquals(
            "(sort ((ts(.select nil ^p nil))(.asTriples)))",
            "((triple s p o1) (triple s p o2) (triple x p z))"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testInterpGraphCtor() {
        AssertInterpretEquals("(graph (triple ^s ^p ^o))", "(graph (triple s p o))")
        AssertInterpretEquals("(graph ^(triple s p o))", "(graph (triple s p o))")

        AssertInterpretEquals("(var one 1)", "1")
        AssertInterpretEquals("(graph ^(s p o))", "(graph (triple s p o))")
        AssertInterpretEquals("(apply graph ^((s p o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(apply graph ^((s p o)(q w e)))", "(graph (triple q w e) (triple s p o))")
        AssertInterpretEquals("(apply graph `((s p \$one)(q w \$one)))", "(graph (triple q w 1) (triple s p 1))")

        AssertInterpretEquals(
            "(var g (graph ^(a = 1) ^(s = 2) ^(d = 3) ))",
            "(graph (triple nil a 1) (triple nil d 3) (triple nil s 2))"
        )
        AssertInterpretEquals(
            "(var g (apply graph ^((a = 1) (s = 2) (d = 3)) ))",
            "(graph (triple nil a 1) (triple nil d 3) (triple nil s 2))"
        )
        AssertInterpretEquals(
            "(var g (apply graph (data (a = 1) (s = 2) (d = 3)) ))",
            "(graph (triple nil a 1) (triple nil d 3) (triple nil s 2))"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testInterpEquals1() {
        AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)")
        AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o2)))", "(graph (triple s p o) (triple s p o2))")
        AssertInterpretEquals("(ts1(.add (triple ^x ^p ^o)))", "(graph (triple s p o) (triple s p o2) (triple x p o))")
        AssertInterpretEquals(
            "(ts1(.add (triple ^s ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p o))"
        )
        AssertInterpretEquals(
            "(ts1(.add (triple ^x ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p 99) (triple x p o))"
        )

        AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)")
        AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o2)))", "(graph (triple s p o) (triple s p o2))")
        AssertInterpretEquals("(ts2(.add (triple ^x ^p ^o)))", "(graph (triple s p o) (triple s p o2) (triple x p o))")
        AssertInterpretEquals(
            "(ts2(.add (triple ^s ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p o))"
        )
        AssertInterpretEquals(
            "(ts2(.add (triple ^x ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p 99) (triple x p o))"
        )

        AssertInterpretEquals("(equal? (asString ts1) (asString ts2))", "true")
        AssertInterpretEquals("(equal? (asString ts2) (asString ts1))", "true")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpEquals2() {
        AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)")
        AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o2)))", "(graph (triple s p o) (triple s p o2))")
        AssertInterpretEquals("(ts1(.add (triple ^x ^p ^o)))", "(graph (triple s p o) (triple s p o2) (triple x p o))")
        AssertInterpretEquals(
            "(ts1(.add (triple ^s ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p o))"
        )
        AssertInterpretEquals(
            "(ts1(.add (triple ^x ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p 99) (triple x p o))"
        )

        AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)")
        AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o2)))", "(graph (triple s p o) (triple s p o2))")
        AssertInterpretEquals("(ts2(.add (triple ^x ^p ^o)))", "(graph (triple s p o) (triple s p o2) (triple x p o))")
        AssertInterpretEquals(
            "(ts2(.add (triple ^s ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p o))"
        )
        AssertInterpretEquals(
            "(ts2(.add (triple ^x ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p 99) (triple x p o))"
        )

        AssertInterpretEquals("(equal? (asString ts1) (asString ts2))", "true")
        AssertInterpretEquals("(equal? (asString ts2) (asString ts1))", "true")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpEquals3() {
        AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)")
        AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)")
        AssertInterpretEquals("(equal? (asString ts1) (asString ts2))", "true")
        AssertInterpretEquals("(equal? (asString ts2) (asString ts1))", "true")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpEquals4() {
        AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)")
        AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o2)))", "(graph (triple s p o) (triple s p o2))")
        AssertInterpretEquals("(ts1(.add (triple ^x ^p ^o)))", "(graph (triple s p o) (triple s p o2) (triple x p o))")
        AssertInterpretEquals(
            "(ts1(.add (triple ^s ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p o))"
        )

        AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)")
        AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o2)))", "(graph (triple s p o) (triple s p o2))")
        AssertInterpretEquals("(ts2(.add (triple ^x ^p ^o)))", "(graph (triple s p o) (triple s p o2) (triple x p o))")
        AssertInterpretEquals(
            "(ts2(.add (triple ^s ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p o))"
        )
        AssertInterpretEquals(
            "(ts2(.add (triple ^x ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p 99) (triple x p o))"
        )

        AssertInterpretEquals("(equal? (asString ts1) (asString ts1))", "true")
        AssertInterpretEquals("(equal? (asString ts2) (asString ts2))", "true")

        AssertInterpretEquals("(equal? (asString ts1) (asString ts2))", "nil")
        AssertInterpretEquals("(equal? (asString ts2) (asString ts1))", "nil")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpEquals5() {
        AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)")
        AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")

        AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)")

        AssertInterpretEquals("(equal? ts1 ts2)", "nil")
        AssertInterpretEquals("(equal? ts2 ts1)", "nil")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpCondition() {
        // #TODO AssertInterpretEquals("(defvar ^isObject99 (lambda (s o p) (equal? p 99)))", "<EagerProc: <anonymous lambda>>");
        AssertInterpretEquals("(defvar ^ts (graph))", "(graph)")
        AssertInterpretEquals("(ts(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts(.add (triple ^s ^p ^o2)))", "(graph (triple s p o) (triple s p o2))")
        AssertInterpretEquals("(ts(.add (triple ^x ^p ^o)))", "(graph (triple s p o) (triple s p o2) (triple x p o))")
        AssertInterpretEquals(
            "(ts(.add (triple ^s ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p o))"
        )
        AssertInterpretEquals(
            "(ts(.add (triple ^x ^p 99)))",
            "(graph (triple s p 99) (triple s p o) (triple s p o2) (triple x p 99) (triple x p o))"
        )

        AssertInterpretEquals("(length (ts(.asTriples)))", "5")


        /* #TODO when select takes a lambda again
		AssertInterpretEquals("(defvar ^result (ts(.select nil nil nil isObject99))))",
				"(graph)");
		AssertInterpretEquals("(defvar ^result (ts(.select ^s nil nil isObject99))))",
				"(graph)");
		AssertInterpretEquals("(result(.asTriples))", "((triple s p 99))");
		AssertInterpretEquals("(defvar ^result (ts(.select ^s ^p nil isObject99))))",
				"(graph)");
		AssertInterpretEquals("(result(.asTriples))", "((triple s p 99))");
		AssertInterpretEquals("(defvar ^result (ts(.select ^s ^p 99 isObject99))))",
				"(graph)");
		AssertInterpretEquals("(result(.asTriples))", "((triple s p 99))");
		*/
    }

    @kotlin.Throws(Exception::class)
    fun testInterpConditionWithVar() {
        AssertInterpretEquals("(defvar ^ninenine 99)", "99")
        AssertInterpretEquals(
            "(defvar ^isObject99 (lambda (s p o) (equal? o 99)))",
            "<EagerProc: <anonymous lambda>>"
        )
        AssertInterpretEquals("(defvar ^ts (graph))", "(graph)")
        AssertInterpretEquals("(ts(.add (triple ^s ^p ^o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts(.add (triple ^x ^p ^o)))", "(graph (triple s p o) (triple x p o))")
        AssertInterpretEquals(
            "(ts(.add (triple ^s ^p ninenine)))",
            "(graph (triple s p 99) (triple s p o) (triple x p o))"
        )
        AssertInterpretEquals(
            "(ts(.add (triple ^x ^p ninenine)))",
            "(graph (triple s p 99) (triple s p o) (triple x p 99) (triple x p o))"
        )
        AssertInterpretEquals("(defvar ^result (ts(.select ^s ^p ninenine isObject99))))", "(graph (triple s p 99))")
        AssertInterpretEquals("(result(.asTriples))", "((triple s p 99))")
        AssertInterpretEquals(
            "((SetList.equal?)(ts(.asTriples)) (list (triple ^s ^p ^o) (triple ^x ^p ^o) (triple ^x ^p ninenine) (triple ^s ^p ninenine)))",
            "true"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testInterpStoreConstruction() {
        AssertInterpretEquals(
            "(defvar ^noop (lambda (&rest args)))",
            "<EagerProc: <anonymous lambda>>"
        )
        AssertInterpretEquals("(graph ^(s p o))", "(graph (triple s p o))")
        AssertInterpretEquals("(defvar ^ts (graph ^(s p o)))", "(graph (triple s p o))")
        AssertInterpretEquals(
            "(ts(.select ^s nil nil noop)))",
            "(graph (triple s p o))"
        ) // #TODO when select takes lambda
        AssertInterpretEquals("(ts(.asTriples)))", "((triple s p o))")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpStoreRemove() {
        AssertInterpretEquals("(defvar ^ts (graph ^(s p o)))", "(graph (triple s p o))")
        AssertInterpretEquals("(ts(.asTriples)))", "((triple s p o))")
        AssertInterpretEquals("(ts(.remove (triple ^s ^p ^o))))", "(graph)")
        AssertInterpretEquals("(ts(.asTriples)))", "nil")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpStoreConstructionMulti() {
        AssertInterpretEquals(
            "(defvar ^noop (lambda (&rest args)))",
            "<EagerProc: <anonymous lambda>>"
        )
        AssertInterpretEquals("(graph ^(s p o) ^(s b c))", "(graph (triple s b c) (triple s p o))")
        AssertInterpretEquals("(defvar ^ts (graph ^(s p o) ^(s b c)))", "(graph (triple s b c) (triple s p o))")
        AssertInterpretEquals(
            "(ts(.select ^s nil nil noop)))",
            "(graph (triple s b c) (triple s p o))"
        ) // #TODO selct with lamda
        AssertInterpretEquals("(length (ts(.asTriples))))", "2")
        AssertInterpretEquals(
            "((SetList.equal?) (ts(.asTriples)) (list (tripleq s p o) (tripleq s b c)))",
            "true"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testInterpTripleDict() {
        AssertInterpretEquals("((dict(.a = 3)(.b = 5))(.asTriples 100))", "((triple 100 b 5) (triple 100 a 3))")
        AssertInterpretEquals("(defvar ^thedict (dict(.a =3)(.b =5)))", "(dict (.a = 3) (.b = 5))")
        AssertInterpretEquals("((thedict(.asGraph 101))(.asTriples))", "((triple 101 b 5) (triple 101 a 3))")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpTriplesClasses() {
        AssertInterpretEquals("(23(.asTriples .self))", "((triple 23 type <class Bignum (Builtin)>))")
        AssertInterpretEquals(
            "('X'(.asTriples .self))",
            "((triple X type <class String (Builtin)>))"
        )
        BadAssertInterpretEquals("(^(a =e)(.asTriples .self))")
        AssertInterpretEquals(
            "(^sym(.asTriples .self))",
            "((triple sym type <class SimpleSymbol (Symbol)>))"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testInterpGraphClasses() {
        AssertInterpretEquals(
            "((23(.asGraph .self))(.asTriples))",
            "((triple 23 type <class Bignum (Builtin)>))"
        )
        AssertInterpretEquals(
            "(('X'(.asGraph .self))(.asTriples))",
            "((triple X type <class String (Builtin)>))"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testInterpSubjects() {
        AssertInterpretEquals(
            "(defvar ^db (graph ^(a p o) ^(c c f) ^(a s d) ))",
            "(graph (triple a p o) (triple a s d) (triple c c f))"
        )
        AssertInterpretEquals("(db(.subjects)))", "(a c)")
    }

    @kotlin.Throws(Exception::class)
    fun testInterpPredicates() {
        AssertInterpretEquals(
            "(defvar ^db (graph ^(a s o) ^(c c f) ^(a p d) ))",
            "(graph (triple a p d) (triple a s o) (triple c c f))"
        )
        AssertInterpretEquals("(db(.predicates ^a)))", "(p s)")
    }
}
