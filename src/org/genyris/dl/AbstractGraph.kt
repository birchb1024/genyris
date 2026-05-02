package org.genyris.dl

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import kotlin.collections.ArrayList
import kotlin.collections.MutableIterator

abstract class AbstractGraph : Atom() {
    abstract fun subjects(NIL: Exp?): Exp?

    abstract fun asTripleList(NIL: Exp?): Exp?

    abstract fun remove(triple: Triple?)

    abstract fun empty(): Boolean

    abstract fun getList(subject: Symbol?, predicate: Symbol?, NIL: Exp?): Exp?

    @kotlin.Throws(GenyrisException::class)
    abstract fun get(subject: Symbol?, predicate: Symbol?): Exp?

    @kotlin.Throws(GenyrisException::class)
    abstract fun select(
        subject: Symbol?, predicate: Symbol?, `object`: Exp?, condition: Closure?,
        env: Environment?
    ): AbstractGraph

    abstract fun add(t: Triple?)

    abstract fun remove(subject: Symbol?, predicate: Symbol?, `object`: Exp?)

    abstract fun contains(subject: Symbol?, predicate: Symbol?, `object`: Exp?): Triple?

    abstract fun length(): Int

    abstract override fun getBuiltinClassSymbol(table: Internable?): Symbol?

    abstract override fun toString(): String

    abstract fun iterator(): MutableIterator<*>

    abstract fun predicates(subject: Symbol?, NIL: Exp?): Exp?

    abstract fun put(subject: Symbol?, predicate: Symbol?, `object`: Exp?)

    override fun equals(compare: Any?): Boolean {
        if (this === compare) {
            return true
        }
        if (compare !is AbstractGraph) {
            return false
        }
        return this.toString() == compare.toString() // TODO is this inefficient ?
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitGraph(this)
    }

    @kotlin.Throws(GenyrisException::class)
    fun difference(toRemove: AbstractGraph?): AbstractGraph {
        return this
    }

    /* #TODO this code is bogus:
    	AbstractGraph result =  new GraphHashSimple();
    	Iterator iter = iterator();
    	while(iter.hasNext()) {
    		Triple item = (Triple)iter.next();
    		remove(item.subject, item.predicate, item.object);
    	}
    	return result;
    }*/
    @kotlin.Throws(GenyrisException::class)
    fun union(other: AbstractGraph): AbstractGraph {
        val result: AbstractGraph = GraphHashSimple()
        var iter = iterator()
        while (iter.hasNext()) {
            val item = iter.next() as Triple?
            result.add(item)
        }
        iter = other.iterator()
        while (iter.hasNext()) {
            val item = iter.next() as Triple?
            result.add(item)
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    abstract fun toSortedTriplesArray(): ArrayList<Triple?>?

    @kotlin.Throws(GenyrisException::class)
    override fun makeEnvironment(parent: Environment?): Environment {
        return GraphEnvironment(parent, this)
    }

    @kotlin.Throws(GenyrisException::class)
    fun shift(sub: Symbol?, env: Environment): Exp? {
        val NIL: Symbol? = env.getNil()
        val g: AbstractGraph
        val result = GraphHashSimple()
        val subjects = this.subjects(NIL)
        // an empty graph, return nil
        if (subjects === NIL) {
            return NIL
        }
        if (subjects !is Pair) {
            throw GenyrisException("subjects are not a list! This should never happen.")
        }
        // a graph with just one subject, NIL, return a List of values
        val subs = subjects
        if (subs.length(NIL) == 1 && subs.car() == NIL) {
            return this.getList(NIL, sub, NIL)
        }
        // strip out all the other triples, leave the ones with this subect, and set subjects to NIL
        g = this.select(sub, null, null, null, env)
        val iter = g.iterator()
        while (iter.hasNext()) {
            val item = iter.next() as Triple
            result.add(Triple(NIL, item.predicate, item.`object`))
        }
        return result
    }
}