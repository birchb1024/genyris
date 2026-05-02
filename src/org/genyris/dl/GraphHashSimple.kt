package org.genyris.dl

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import org.genyris.core.Exp
import org.genyris.core.Internable
import org.genyris.core.Pair
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import java.util.*

class GraphHashSimple : AbstractGraph() {
    var subjects: Multimap<Symbol?, Triple?>
    var predicates: Multimap<Symbol?, Triple?>
    var objects: Multimap<Exp?, Triple?>

    init {
        subjects = ArrayListMultimap.create<Symbol?, Triple?>()
        predicates = ArrayListMultimap.create<Symbol?, Triple?>()
        objects = ArrayListMultimap.create<Exp?, Triple?>()
    }

    override fun iterator(): MutableIterator<*> {
        //
        // Iterate over the triples in the Graph.
        //
        return subjects.values().iterator()
    }

    override fun toSortedTriplesArray(): ArrayList<Triple> {
        val triples = subjects.values()
        val triarray = ArrayList<Triple>(triples)
        Collections.sort<Triple?>(triarray)
        return triarray
    }

    override fun toString(): String {
        var result = "(graph"
        val triarray = toSortedTriplesArray()
        for (T in triarray) {
            result += " " + T.toString()
        }
        result += ")"
        return result
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.GRAPH()
    }

    override fun length(): Int {
        return subjects.size()
    }

    override fun contains(S: Symbol?, P: Symbol?, O: Exp?): Triple? {
        val iter: MutableIterator<*> = subjects.get(S).iterator()
        while (iter.hasNext()) {
            val t = iter.next() as Triple
            if (t.predicate == P && t.`object` == O) {
                return t
            }
        }
        return null
    }

    override fun add(t: Triple) {
        if (contains(t.subject, t.predicate, t.`object`) != null) {
            return
        }
        subjects.put(t.subject, t)
        predicates.put(t.predicate, t)
        objects.put(t.`object`, t)
    }

    fun findDuplicateSubjectPredicates() {
        val subPredMap: MutableMap<String?, Triple?> = HashMap<String?, Triple?>()
        val iterTriples: MutableIterator<*> = subjects.values().iterator()
        while (iterTriples.hasNext()) {
            val t = iterTriples.next() as Triple
            val sp = t.subject.getPrintName() + t.predicate.getPrintName()
            if (subPredMap.containsKey(sp)) {
                continue
            }
            subPredMap.put(sp, t)
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun select(
        S: Symbol?, P: Symbol?, O: Exp?,
        condition: Closure?, env: Environment?
    ): AbstractGraph {
        if (condition != null) {
            throw GenyrisException("Closures not supported in Graph.select, sorry.")
        }
        val results = GraphHashSimple()
        // * * *
        if (S == null && P == null && O == null) {
            return this // #TODO Hmm deep copy?
        }
        // Subject, *, *
        if (O == null && P == null) {
            val Ses: MutableIterator<*> = subjects.get(S).iterator()
            while (Ses.hasNext()) {
                val t = Ses.next() as Triple
                results.add(t)
            }
            return results
        }
        // *, Predicate, *
        if (S == null && O == null) {
            val Ps: MutableIterator<*> = predicates.get(P).iterator()
            while (Ps.hasNext()) {
                val t = Ps.next() as Triple
                results.add(t)
            }
            return results
        }
        // *, *, Object
        if (S == null && P == null) {
            val Os: MutableIterator<*> = objects.get(O).iterator()
            while (Os.hasNext()) {
                val t = Os.next() as Triple
                results.add(t)
            }
            return results
        }
        // Subject, Predicate, *
        if (O == null) {
            val Ses: MutableIterator<*> = subjects.get(S).iterator()
            while (Ses.hasNext()) {
                val t = Ses.next() as Triple
                if (t.subject == S && t.predicate == P) {
                    results.add(t)
                }
            }
            return results
        }
        // Subject, *, Object
        if (P == null) {
            val Ses: MutableIterator<*> = subjects.get(S).iterator()
            while (Ses.hasNext()) {
                val t = Ses.next() as Triple
                if (t.subject == S && t.`object` == O) {
                    results.add(t)
                }
            }
            return results
        }
        // *, Predicate, Object
        if (S == null) {
            val Ps: MutableIterator<*> = predicates.get(P).iterator()
            while (Ps.hasNext()) {
                val t = Ps.next() as Triple
                if (t.predicate == P && t.`object` == O) {
                    results.add(t)
                }
            }
            return results
        }
        // Subject Predicate Object
        run {
            val Ses: MutableIterator<*> = subjects.get(S).iterator()
            while (Ses.hasNext()) {
                val t = Ses.next() as Triple
                if (t.predicate == P && t.`object` == O) {
                    results.add(t)
                }
            }
            return results
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun get(S: Symbol, P: Symbol): Exp? {
        val found: MutableList<Triple?> = ArrayList<Any?>()
        // Subject, Predicate, a single object value
        val Ses: MutableIterator<*> = subjects.get(S).iterator()
        while (Ses.hasNext()) {
            val t = Ses.next() as Triple
            if (t.predicate == P) {
                found.add(t)
            }
        }
        if (found.size() == 0) {
            throw GenyrisException("No triples in graph matching " + S.toString() + " " + P.toString())
        }
        if (found.size() > 1) {
            throw GenyrisException("Multiple triples (" + found.size() + ") in graph matching " + S.toString() + " " + P.toString())
        }
        return found.get(0)!!.`object`
    }

    override fun getList(S: Symbol?, P: Symbol?, NIL: Exp?): Exp? {
        var result = NIL
        val Ses: MutableIterator<*> = subjects.get(S).iterator()
        while (Ses.hasNext()) {
            val t = Ses.next() as Triple
            if (t.predicate == P) {
                result = Pair.Companion.cons(t.`object`, result)
            }
        }
        return result
    }

    override fun empty(): Boolean {
        return subjects.isEmpty()
    }

    override fun remove(S: Symbol?, P: Symbol?, O: Exp?) {
        var existing = contains(S, P, O)
        while (existing != null) {
            subjects.remove(S, existing)
            predicates.remove(P, existing)
            objects.remove(O, existing)
            existing = contains(S, P, O)
        }
    }

    override fun remove(t: Triple) {
        remove(t.subject, t.predicate, t.`object`)
    }

    override fun asTripleList(NIL: Exp?): Exp? {
        var result = NIL
        val items: MutableCollection<*> = subjects.values()
        val iter: MutableIterator<*> = items.iterator()
        while (iter.hasNext()) {
            val item = iter.next() as Triple
            result = Pair(
                Triple(item.subject, item.predicate, item.`object`),
                result
            )
        }
        return result
    }

    override fun put(S: Symbol, P: Symbol, O: Exp?) {
        val iter: MutableIterator<*> = subjects.get(S).iterator()
        val existing: MutableList<Triple> = ArrayList<Any?>()
        while (iter.hasNext()) {
            val t = iter.next() as Triple
            if (S == t.subject && P == t.predicate) {
                existing.add(t)
            }
        }
        // so as not to remove while iterating
        for (t in existing) {
            remove(t)
        }
        add(Triple(S, P, O))
    }


    override fun subjects(NIL: Exp?): Exp? {
        val iter: MutableIterator<*> = subjects.values().iterator()
        val Ses: MutableSet<Symbol?> = TreeSet<Any?>()
        while (iter.hasNext()) {
            val t = iter.next() as Triple
            Ses.add(t.subject)
        }
        return getSetAsList(NIL, Ses)
    }

    override fun predicates(S: Symbol?, NIL: Exp?): Exp? {
        val Ps: MutableSet<Symbol?> = TreeSet<Any?>()
        val Ses: MutableIterator<*> = subjects.get(S).iterator()
        while (Ses.hasNext()) {
            val T = Ses.next() as Triple
            Ps.add(T.predicate)
        }
        return getSetAsList(NIL, Ps)
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }

    companion object {
        private fun getSetAsList(NIL: Exp?, Ses: MutableSet<Symbol?>): Exp? {
            val Piter: MutableIterator<*> = Ses.iterator()
            var head = NIL
            var tail: Pair? = null
            while (Piter.hasNext()) {
                val newitem = Pair(Piter.next() as Exp?, NIL)
                if (head === NIL) {
                    tail = newitem
                    head = tail
                    continue
                }
                tail!!.setCdr(newitem)
                tail = newitem
            }
            return head
        }
    }
}

