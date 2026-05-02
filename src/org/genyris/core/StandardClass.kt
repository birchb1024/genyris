package org.genyris.core

import org.genyris.classes.GlobalDescriptions
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.UnboundException
import org.genyris.java.JavaClass

open class StandardClass : Dictionary {
    private val CLASSNAME: SimpleSymbol?
    private val SUPERCLASSES: SimpleSymbol?
    private val SUBCLASSES: SimpleSymbol?

    private val NIL: SimpleSymbol

    constructor(
        classname: Symbol?, symbolicName: Symbol?,
        env: Environment
    ) : super(classname, symbolicName, env) {
        CLASSNAME = env.getSymbolTable().CLASSNAME()
        SUPERCLASSES = env.getSymbolTable().SUPERCLASSES()
        SUBCLASSES = env.getSymbolTable().SUBCLASSES()
        NIL = env.getNil()
    }

    constructor(env: Environment) : super(env) {
        CLASSNAME = env.getSymbolTable().CLASSNAME()
        SUPERCLASSES = env.getSymbolTable().SUPERCLASSES()
        SUBCLASSES = env.getSymbolTable().SUBCLASSES()
        NIL = env.getNil()
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.STANDARDCLASS()
    }

    override fun toString(): String {
        var result = "<class "
        result += this.className
        try {
            result += classListToString(this.superClasses)
            // result += classListToString(getSubClasses());
        } catch (e: AccessException) {
            return this.className + " toString():  " + e.getMessage()
        }
        result += ">"
        return result
    }

    @kotlin.Throws(AccessException::class)
    private fun classListToString(classes: Exp): String {
        var classes = classes
        val result = StringBuffer(" (")
        while (classes !== NIL) {
            val klass = classes.car() as StandardClass
            result.append(klass.className)
            if (classes.cdr() !== NIL) result.append(' ')
            classes = classes.cdr()
        }
        result.append(")")
        return result.toString()
    }

    private val subClasses: Exp
        get() {
            try {
                return lookupVariableShallow(SUBCLASSES)
            } catch (e: UnboundException) {
                return NIL
            }
        }

    @kotlin.Throws(UnboundException::class)
    fun addSuperClass(klass: StandardClass?) {
        if (klass == null) return

        var supers = lookupVariableShallow(SUPERCLASSES)
        supers = Pair(klass, supers)
        setDynamicVariableValueRaw(SUPERCLASSES, supers)
        klass.addSubClass(this)
        // TODO use a list set adding function to avoid duplicates.
    }

    @kotlin.Throws(UnboundException::class)
    fun addSubClass(klass: StandardClass?) {
        if (klass == null) return
        var subs = lookupVariableShallow(SUBCLASSES)
        subs = Pair(klass, subs)
        setDynamicVariableValueRaw(SUBCLASSES, subs)
        // TODO use a list set adding function to avoid duplicate subclasses.
    }

    private val superClasses: Exp
        get() {
            try {
                return lookupVariableShallow(SUPERCLASSES)
            } catch (e: UnboundException) {
                return NIL
            }
        }

    val className: String?
        get() {
            try {
                return lookupVariableShallow(CLASSNAME).toString()
            } catch (e: UnboundException) {
                return "Anonymous"
            }
        }

    @kotlin.Throws(GenyrisException::class)
    fun isSubClass(klass: StandardClass?): Boolean {
        if (klass === this) {
            return true
        }
        val env = getParent()
        var mysubclasses = this.subClasses

        while (mysubclasses !== env.getNil()) {
            assertIsThisObjectAClass(mysubclasses.car())
            val firstClass = mysubclasses.car() as StandardClass
            if (firstClass === klass) {
                return true
            } else if (firstClass.isSubClass(klass)) {
                return true
            }
            mysubclasses = mysubclasses.cdr()
        }
        return false
    }

    @kotlin.Throws(GenyrisException::class)
    fun isInstance(`object`: Exp): Boolean {
        val env = getParent()
        var classes: Exp

        classes = `object`.getClasses(env)
        while (classes !== env.getNil()) {
            assertIsThisObjectAClass(classes.car())
            val klass = classes.car() as StandardClass?
            if (classes.car() === this) {
                return true
            }
            if (isSubClass(klass)) {
                return true
            }
            classes = classes.cdr()
        }
        return false
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitStandardClass(this)
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun mkClass(
            name: String?, env: Environment,
            superClass: StandardClass?
        ): StandardClass {
            val table = env.getSymbolTable()
            val STANDARDCLASS: Symbol? = table.STANDARDCLASS()
            val standardClassDict = env
                .lookupVariableValue(STANDARDCLASS) as StandardClass?
            val classname = table.CLASSNAME()
            val symbolicName = table.internString(name)

            val newClass: StandardClass = makeTheClass(
                env, superClass, table,
                standardClassDict, classname, symbolicName
            )
            GlobalDescriptions.updateClassSingleSuper(
                env, table, symbolicName,
                (if (superClass != null) superClass
                    .lookupVariableShallow(classname) as Symbol? else null)
            )
            return newClass
        }

        @kotlin.Throws(GenyrisException::class)
        private fun makeTheClass(
            env: Environment,
            superClass: StandardClass?, table: Internable,
            standardClassDict: StandardClass?, classname: Symbol?,
            symbolicName: Symbol?
        ): StandardClass {
            val newClass = StandardClass(classname, symbolicName, env)
            newClass.defineVariableRaw(table.SUPERCLASSES(), env.getNil())
            newClass.defineVariableRaw(table.SUBCLASSES(), env.getNil())
            if (superClass != null) newClass.addSuperClass(superClass)
            env.defineVariable(symbolicName, newClass)
            return newClass
        }

        @kotlin.Throws(GenyrisException::class)
        fun makeClass(
            javaClass: Class<*>, env: Environment,
            klassname: Symbol?, superklasses: Exp
        ): JavaClass {
            return makeClass(JavaClass(javaClass, env), env, klassname, superklasses) as JavaClass
        }

        @kotlin.Throws(GenyrisException::class)
        fun makeClass(
            env: Environment, klassname: Symbol?,
            superklasses: Exp
        ): StandardClass {
            return makeClass(StandardClass(env), env, klassname, superklasses)
        }

        @kotlin.Throws(GenyrisException::class)
        fun makeClass(
            newClass: StandardClass, env: Environment,
            klassname: Symbol?, superklasses: Exp
        ): StandardClass {
            var superklasses = superklasses
            val NIL: Exp = env.getNil()
            newClass.defineVariableRaw(env.getSymbolTable().CLASSNAME(), klassname)
            newClass.defineVariableRaw(env.getSymbolTable().SUBCLASSES(), NIL)
            if (superklasses === NIL) superklasses = Pair(env.getSymbolTable().THING(), NIL)

            newClass.defineVariableRaw(
                env.getSymbolTable().SUPERCLASSES(),
                lookupClasses(env, superklasses)
            )
            var sklist = superklasses
            while (sklist !== NIL) {
                val possibleClass = env.lookupVariableValue(sklist.car() as Symbol?)
                assertIsThisObjectAClass(possibleClass)
                val superClass = possibleClass as StandardClass
                var subklasses: Exp? = NIL
                try {
                    subklasses = superClass.lookupVariableShallow(
                        env
                            .getSymbolTable().SUBCLASSES()
                    )
                } catch (ignore: UnboundException) {
                    superClass.defineVariable(
                        env.getSymbolTable().SUBCLASSES(),
                        NIL
                    )
                }
                superClass.setDynamicVariableValueRaw(
                    env.getSymbolTable()
                        .SUBCLASSES(), Pair(newClass, subklasses)
                )
                sklist = sklist.cdr()
            }

            env.defineVariable(klassname, newClass)
            GlobalDescriptions.updateClass(
                env, env.getSymbolTable(), klassname,
                superklasses
            )
            return newClass
        }

        @kotlin.Throws(GenyrisException::class)
        private fun lookupClasses(env: Environment, superklasses: Exp): Exp {
            var superklasses = superklasses
            var result: Exp = env.getNil()
            while (superklasses !== env.getNil()) {
                result = Pair(
                    env.lookupVariableValue(
                        superklasses
                            .car() as Symbol?
                    ), result
                )
                superklasses = superklasses.cdr()
            }
            return result
        }

        @kotlin.Throws(GenyrisException::class)
        fun assertIsThisObjectAClass(firstClass: Exp?) {
            if (firstClass !is StandardClass) {
                throw GenyrisException(firstClass.toString() + " is not a class.")
            }
        }
    }
}
