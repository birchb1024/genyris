// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.format.BasicFormatter
import org.genyris.format.Formatter
import org.genyris.interp.Interpreter
import org.genyris.io.*
import java.io.StringWriter

class LexTest : TestCase() {
    var _table: SymbolTable = SymbolTable()

    public override fun setUp() {
        _table.init(null)
    }

    @kotlin.Throws(GenyrisException::class)
    private fun excerciseNextTokenInt(expected: Exp?, toparse: String) {
        _table.init(null)
        val lexer = Lex(
            UngettableInStream(StringInStream(toparse)), _table
        )
        assertEquals(expected, lexer.nextToken())
    }

    @kotlin.Throws(GenyrisException::class)
    private fun excerciseNextTokenBignum(expected: Exp, toparse: String) {
        _table.init(null)
        val lexer = Lex(
            UngettableInStream(StringInStream(toparse)), _table
        )
        Assert.assertEquals(expected.toString(), lexer.nextToken().toString())
    }

    @kotlin.Throws(GenyrisException::class)
    private fun excerciseNextTokenExp2Times(expected: Exp, toparse: String) {
        _table.init(null)
        val lexer = Lex(
            UngettableInStream(StringInStream(toparse)), _table
        )
        lexer.nextToken()
        Assert.assertEquals(expected.toString(), lexer.nextToken().toString())
    }

    @kotlin.Throws(GenyrisException::class)
    private fun excerciseNextTokenExp(expected: Exp, toparse: String) {
        _table.init(null)
        val lexer = Lex(
            UngettableInStream(StringInStream(toparse)), _table
        )
        val result = lexer.nextToken()
        Assert.assertEquals(expected.toString(), result.toString())
        Assert.assertEquals(expected.getClass(), result.getClass())
    }

    private fun excerciseBadNextTokenExp(toparse: String) {
        _table.init(null)
        val lexer = Lex(
            UngettableInStream(StringInStream(toparse)), _table
        )
        val result: Exp?
        try {
            result = lexer.nextToken()
            fail("got " + result + " when looking for exception.")
        } catch (ex: GenyrisException) {
        }
    }

    @kotlin.Throws(Exception::class)
    fun testLexNumbers() {
        excerciseNextTokenInt(Bignum("12"), "12")
        excerciseNextTokenInt(Bignum("-12"), "-12")
        excerciseNextTokenBignum(Bignum("12.34"), "12.34")
        excerciseNextTokenBignum(Bignum("-12.34"), "-12.34")
    }

    @kotlin.Throws(Exception::class)
    fun testNUmbers() {
        excerciseNextTokenInt(Bignum("12"), "   12")
        excerciseNextTokenInt(Bignum("-12"), "\t\t-12")
        excerciseNextTokenBignum(Bignum("12.34"), "\n\n12.34")
        excerciseNextTokenBignum(Bignum("-12.34"), "\r\u000c -12.34")
    }

    @kotlin.Throws(Exception::class)
    fun testLexIdent1() {
        excerciseNextTokenExp(SimpleSymbol("foo"), "foo")
        excerciseNextTokenExp(SimpleSymbol("foo*bar"), "foo\\*bar")
        excerciseNextTokenExp(SimpleSymbol("quux"), "\n\nquux")
        excerciseNextTokenExp(EscapedSymbol("123"), "  \t|123|")
        excerciseNextTokenExp(SimpleSymbol("DYNAMIC_TOKEN"), "  \t .x")
    }

    @kotlin.Throws(Exception::class)
    fun testLexIdentEscaped() {
        try {
            excerciseNextTokenExp2Times(SimpleSymbol("q"), "|werw\t|q|")
            fail()
        } catch (ignore: GenyrisException) {
        }
        excerciseNextTokenExp2Times(EscapedSymbol("q"), "|werw| |q|")

        excerciseNextTokenExp2Times(SimpleSymbol("q|"), "|werw |q|")
        excerciseNextTokenExp(
            EscapedSymbol("with a space in it"),
            "|with a space in it|"
        )
        excerciseNextTokenExp(
            EscapedSymbol("http://foo/bar space/#123"),
            "|http://foo/bar space/#123|"
        )
        excerciseNextTokenExp(
            URISymbol("http://foo/bar%20space/#123"),
            "|http://foo/bar%20space/#123|"
        )

        excerciseNextTokenExp(EscapedSymbol("foo"), "|foo|")
        excerciseNextTokenExp(EscapedSymbol("fo|o"), "|fo\\|o|")
        excerciseNextTokenExp(EscapedSymbol("foo*bar"), "|foo\\*bar|")
        excerciseNextTokenExp(EscapedSymbol("quux"), "\n\n|quux|")
        excerciseNextTokenExp(EscapedSymbol("123"), "  \t|123|")
        excerciseNextTokenExp(
            URISymbol("http://foo/bar#123"),
            "|http://foo/bar#123|"
        )
        excerciseNextTokenExp(
            URISymbol("http://foo/b:ar#123"),
            "|http://foo/b\\:ar#123|"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testLexIdentMinus() {
        excerciseNextTokenExp(SimpleSymbol("-"), "- 3")
        excerciseNextTokenExp(SimpleSymbol("-f"), "-f")
        excerciseNextTokenExp(SimpleSymbol("--"), "--")
    }

    @kotlin.Throws(Exception::class)
    fun testLexCommentStrip() {
        excerciseNextTokenExp(SimpleSymbol("X"), "X # foo")
        excerciseNextTokenExp(SimpleSymbol("Y"), "# stripped \nY")
        excerciseNextTokenExp(Bignum(12), "   \n\t\u000c      # stripped \n12")
    }

    @kotlin.Throws(Exception::class)
    fun testLexString() {
        excerciseNextTokenExp(StrinG("str"), "'str'")
        excerciseNextTokenExp(StrinG("s\nr"), "'s\nr'")
        excerciseNextTokenExp(StrinG("s\nr"), "'s\nr'")
        excerciseNextTokenExp(StrinG("s\nr"), "'s\nr'")
        excerciseNextTokenExp(StrinG("\n\t\u000c\r\\"), "'\n\t\u000c\r\\\\'")
        excerciseNextTokenExp(StrinG("s1-"), "'\\s\\1\\-'")
        excerciseNextTokenExp(StrinG("\u0007\n\r\t\u000c"), "'\\a\\n\\r\\t\\f'")
        excerciseNextTokenExp(StrinG("\\"), "'\\\\'")
        excerciseNextTokenExp(StrinG("\""), "'\\\"'")
        excerciseNextTokenExp(StrinG("\\"), "'\\\\'")
        excerciseNextTokenExp(StrinG("\u001b"), "\"\\e\"")
        excerciseNextTokenExp(StrinG(""), "\"")
    }

    @kotlin.Throws(Exception::class)
    fun testLexString2() {
        excerciseBadNextTokenExp("\"\\")
    }

    @kotlin.Throws(Exception::class)
    fun testCombination1() {
        val lexer = Lex(
            UngettableInStream(
                StringInStream(
                    "int 12 double\n 12.34\r\n -12.34e5 \"string\" "
                )
            ), _table
        )
        Assert.assertEquals(
            SimpleSymbol("int").toString(), lexer.nextToken()
                .toString()
        )
        assertEquals(Bignum("12"), lexer.nextToken())
        Assert.assertEquals(
            SimpleSymbol("double").toString(), lexer.nextToken()
                .toString()
        )
        assertEquals(Bignum("12.34"), lexer.nextToken())
        assertEquals(Bignum(-12.34e5), lexer.nextToken())
        assertEquals(StrinG("string"), lexer.nextToken())
    }

    @kotlin.Throws(Exception::class)
    private fun excerciseListParsing(toParse: String) {
        val NIL: SimpleSymbol = NilSymbol()
        val table = SymbolTable()
        table.init(NIL)
        val input: InStream = UngettableInStream(StringInStream(toParse))
        val parser = Parser(table, input)
        val result = parser.read()

        val out = StringWriter()
        val formatter: Formatter = BasicFormatter(out)
        result.acceptVisitor(formatter)
        Assert.assertEquals(toParse, out.getBuffer().toString())
    }

    @kotlin.Throws(Exception::class)
    private fun excerciseListParsingLisp(toParse: String, expected: String?) {
        val NIL: SimpleSymbol = NilSymbol()
        val table = SymbolTable()
        table.init(NIL)
        val input: InStream = UngettableInStream(StringInStream(toParse))
        val parser = Parser(table, input, '$', '.', ';')
        val result = parser.read()

        val out = StringWriter()
        val formatter: Formatter = BasicFormatter(out)
        result.acceptVisitor(formatter)
        Assert.assertEquals(expected, out.getBuffer().toString())
    }

    @kotlin.Throws(Exception::class)
    fun testLists1() {
        excerciseListParsing("(1 2 3)")
        excerciseListParsing("(1 (2) 3)")
        excerciseListParsing("(1 (2) 3 (4 (5 (6))))")

        excerciseListParsing("(1 = 2)")
        excerciseListParsing("(1 2 = 3)")

        excerciseListParsing("('a' 1.2 30000 foo)")
        excerciseListParsing("('a' 1.2 30000 foo (1 2 = 3))")
        excerciseListParsing("('a' 1.2 30000 foo (1 2 = 3) (1 (2) 3 (4 (5 (6)))))")
        excerciseListParsing("(defun my-func (x) (cons x x))")
    }

    @kotlin.Throws(Exception::class)
    fun testListsLisp() {
        excerciseListParsingLisp("(1 . 2)", "(1 = 2)")
        excerciseListParsingLisp("(1 2 . 3)", "(1 2 = 3)")

        excerciseListParsingLisp("('a' 1.2 30000 foo)", "('a' 1.2 30000 foo)")
        excerciseListParsingLisp(
            "('a' 1.2 30000 foo (1 2 . 3))",
            "('a' 1.2 30000 foo (1 2 = 3))"
        )
        excerciseListParsingLisp(
            "('a' 1.2 30000 foo (1 2 . 3) (1 (2) 3 (4 (5 (6)))))",
            "('a' 1.2 30000 foo (1 2 = 3) (1 (2) 3 (4 (5 (6)))))"
        )
    }

    @kotlin.Throws(Exception::class)
    private fun excerciseSpecialParsing(toParse: String, expected: String?) {
        val interpreter = Interpreter()
        interpreter.init(false, "excerciseSpecialParsing")

        val input: InStream = UngettableInStream(StringInStream(toParse))
        val parser = Parser(interpreter.getSymbolTable(), input)
        val result = parser.read()

        val out = StringWriter()
        val formatter: Formatter = BasicFormatter(out)
        result.acceptVisitor(formatter)
        Assert.assertEquals(expected, out.getBuffer().toString())
    }

    @kotlin.Throws(Exception::class)
    private fun excerciseBadSpecialParsing(toParse: String) {
        val interpreter = Interpreter()
        interpreter.init(false, "excerciseBadSpecialParsing")

        val input: InStream = UngettableInStream(StringInStream(toParse))
        val parser = Parser(interpreter.getSymbolTable(), input)
        try {
            parser.read()
        } catch (e: ParseException) {
            return
        }
        fail()
    }

    @kotlin.Throws(Exception::class)
    fun testSpecialLexQuote() {
        excerciseSpecialParsing("^a", "(quote a)")
        excerciseSpecialParsing("^12.34", "(quote 12.34)")
        excerciseSpecialParsing("^'str'", "(quote 'str')")
        excerciseSpecialParsing("^(1 2)", "(quote (1 2))")
        excerciseSpecialParsing("^(1 = 2)", "(quote (1 = 2))")
        excerciseSpecialParsing("^(1=2)", "(quote (1 = 2))")
        excerciseSpecialParsing("^(1 =2)", "(quote (1 = 2))")
        excerciseSpecialParsing("^(a =b)", "(quote (a = b))")
        excerciseSpecialParsing("^(a =b)", "(quote (a = b))")
        excerciseSpecialParsing("^(a = b)", "(quote (a = b))")
        excerciseSpecialParsing("^(a = (3))", "(quote (a = (3)))")
    }

    @kotlin.Throws(Exception::class)
    fun testMixedQuotes() {
        excerciseSpecialParsing("'\"'", "'\"'")
        excerciseSpecialParsing("\"'\"", "\"'\"")
        excerciseSpecialParsing("'123\"456'", "'123\"456'")
    }

    @kotlin.Throws(Exception::class)
    fun testSpecialLexBackQuote() {
        excerciseSpecialParsing("`12.34", "(template 12.34)")
        excerciseSpecialParsing("`'str'", "(template 'str')")
        excerciseSpecialParsing("`(1 2)", "(template (1 2))")
        excerciseSpecialParsing("`(1 = 2)", "(template (1 = 2))")
        excerciseSpecialParsing("``(1 = 2)", "(template (template (1 = 2)))")
        excerciseSpecialParsing("`(1 = 2)`", "(template (1 = 2))")
    }

    @kotlin.Throws(Exception::class)
    fun testSpecialLexComma() {
        excerciseSpecialParsing("\$a", "(dollar a)")
    }

    @kotlin.Throws(Exception::class)
    fun testSpecialLexCommaAt() {
        excerciseSpecialParsing("$@12", "(dollar-at 12)")
    }

    @kotlin.Throws(Exception::class)
    fun testSquarebracket() {
        excerciseSpecialParsing("[]", "(squareBracket)")
        excerciseSpecialParsing("[1]", "(squareBracket 1)")
        excerciseSpecialParsing("[1 2]", "(squareBracket 1 2)")
        excerciseSpecialParsing("[^1]", "(squareBracket (quote 1))")
        excerciseSpecialParsing("[[2]3]", "(squareBracket (squareBracket 2) 3)")
        excerciseSpecialParsing("['foo']", "(squareBracket 'foo')")
        excerciseSpecialParsing("[w ^e]", "(squareBracket w (quote e))")
        excerciseSpecialParsing("[(1 2 3)]", "(squareBracket (1 2 3))")
    }

    @kotlin.Throws(Exception::class)
    fun testCurlybracket() {
        excerciseSpecialParsing("{}", "(curlyBracket)")
        excerciseSpecialParsing("{1}", "(curlyBracket 1)")
        excerciseSpecialParsing("{1 2}", "(curlyBracket 1 2)")
        excerciseSpecialParsing("{^1}", "(curlyBracket (quote 1))")
        excerciseSpecialParsing("{{2}3}", "(curlyBracket (curlyBracket 2) 3)")
        excerciseSpecialParsing("{'foo'}", "(curlyBracket 'foo')")
        excerciseSpecialParsing("{w ^e}", "(curlyBracket w (quote e))")
        excerciseSpecialParsing("{(1 2 3)}", "(curlyBracket (1 2 3))")
    }

    @kotlin.Throws(Exception::class)
    fun testSemi() {
        excerciseSpecialParsing("(a;b)", "((a .b))")
        excerciseSpecialParsing("(a;b;c)", "(((a .b) .c))")
        excerciseSpecialParsing("(a;b;c;d;e;f;g)", "(((((((a .b) .c) .d) .e) .f) .g))")
        excerciseSpecialParsing("(.f;g)", "((.f .g))")

        excerciseSpecialParsing("((f);g)", "(((f) .g))")
        excerciseSpecialParsing("((f);g;h;i)", "(((((f) .g) .h) .i))")
        excerciseSpecialParsing("((f(g(h)));i;j)", "((((f (g (h))) .i) .j))")
        excerciseSpecialParsing("(quote 2;w)", "(quote (2 .w))")
        excerciseSpecialParsing("(a;x = 33)", "((a .x) = 33)")

        excerciseBadSpecialParsing("(.f;.g)")
        excerciseBadSpecialParsing("(x;.y)")
        excerciseBadSpecialParsing("(a;1)")
        excerciseBadSpecialParsing("(2;3)")
    }

    @kotlin.Throws(Exception::class)
    fun testPling() {
        excerciseSpecialParsing("(a!b)", "((pling a b))")
        excerciseSpecialParsing("(a!b!c)", "((pling (pling a b) c))")
        excerciseSpecialParsing("(a!b!c!d!e!f!g)", "((pling (pling (pling (pling (pling (pling a b) c) d) e) f) g))")
        excerciseSpecialParsing("(.f!g)", "((pling .f g))")

        excerciseSpecialParsing("((f)!g)", "((pling (f) g))")
        excerciseSpecialParsing("((f)!g!h!i)", "((pling (pling (pling (f) g) h) i))")
        excerciseSpecialParsing("((f(g(h)))!i!j)", "((pling (pling (f (g (h))) i) j))")
        excerciseSpecialParsing("(quote 2!w)", "(quote (pling 2 w))")
        excerciseSpecialParsing("(a!x = 33)", "((pling a x) = 33)")

        // focus on LHS
        excerciseSpecialParsing("(3!4)", "((pling 3 4))")
        excerciseSpecialParsing("('g'!4)", "((pling 'g' 4))")
        excerciseSpecialParsing("((5)!4)", "((pling (5) 4))")
        excerciseSpecialParsing("(nil!4)", "((pling nil 4))")
        excerciseSpecialParsing("(.f!4)", "((pling .f 4))")
        excerciseSpecialParsing("([g]!4)", "((pling (squareBracket g) 4))")
        excerciseSpecialParsing("({h}!4)", "((pling (curlyBracket h) 4))")

        // focus on RHS
        excerciseSpecialParsing("(2!3)", "((pling 2 3))")
        excerciseSpecialParsing("(2!'g')", "((pling 2 'g'))")
        excerciseSpecialParsing("(2!(5))", "((pling 2 (5)))")
        excerciseSpecialParsing("(2!nil)", "((pling 2 nil))")
        excerciseSpecialParsing("(2!.f)", "((pling 2 .f))")
        excerciseSpecialParsing("(2![g])", "((pling 2 (squareBracket g)))")
        excerciseSpecialParsing("(2!{h})", "((pling 2 (curlyBracket h)))")

        excerciseSpecialParsing("($(expression!filename))", "((dollar ((pling expression filename))))")
        excerciseSpecialParsing("(\$expression!filename)", "((pling (dollar expression) filename))")
        excerciseSpecialParsing("(find-abs-path args!left)", "(find-abs-path (pling args left))")


        // empties
        excerciseBadSpecialParsing("(!)")
        excerciseBadSpecialParsing("(1!)")
        excerciseBadSpecialParsing("(1!!2)")
        excerciseBadSpecialParsing("(1!!!3)")


        // mixed multiples
        excerciseSpecialParsing(
            "(nil!2!'3'!(4)![5]!{6})",
            "((pling (pling (pling (pling (pling nil 2) '3') (4)) (squareBracket 5)) (curlyBracket 6)))"
        )

        // examples
        // nth
        excerciseSpecialParsing("(^(a s d)!1)", "((pling (quote (a s d)) 1))")
        // assoc
        excerciseSpecialParsing("(^((a = 1)(s = 2))!s)", "((pling (quote ((a = 1) (s = 2))) s))")

        excerciseBadSpecialParsing("(a!s;2)")
    }
}
