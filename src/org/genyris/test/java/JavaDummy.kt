package org.genyris.test.java


class JavaDummy {
    var int: Int = 42
    var longField: Long = 42000000
    var charField: Char = 'G'
    var floatField: Float
    var doubleField: Double = 4.2e42
    var booleanField: Boolean = false
    var byteField: Int = 0xFF
    var shortField: Int = 2
    var stringField: String? = "FOO!"

    var privateField: Int = 42

    init {
        floatField = 4.2.toFloat()

        privateField = privateField * 2 // stop compiler warning
    }

    fun method1(args: Array<String?>?): Array<String?>? {
        return args
    }

    @kotlin.Throws(Exception::class)
    fun failmethod2(): Int {
        val x = 45
        if (x == 45) throw Exception("death in failmethod2()")
        return 43
    }

    companion object {
        //
        // Class for testing the FFI code with.
        //
        var staticField: Int = 123
        fun staticMethod1(count: Int): Array<String?> {
            val retval = arrayOfNulls<String>(count)
            for (i in 0..<count) {
                retval[i] = Integer.toString(i)
            }
            return retval
        }

        fun staticMethod2(count: Int): Array<Int?> {
            val retval = arrayOfNulls<Int>(count)
            for (i in 0..<count) {
                retval[i] = Integer.valueOf(i)
            }
            return retval
        }

        @kotlin.Throws(Exception::class)
        fun failmethod1(): Int {
            val x = 45
            if (x == 45) throw Exception("death in failmethod1()")
            return 43
        }
    }
}
