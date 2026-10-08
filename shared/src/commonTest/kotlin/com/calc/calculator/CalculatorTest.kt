package com.calc.calculator

import kotlin.test.Test
import kotlin.test.assertEquals

private fun eval(expr: String): CalculatorState {
    var s = initialState
    for (c in expr) {
        s = when (c) {
            in '0'..'9' -> reduce(s, CalculatorAction.Digit(c))
            '.' -> reduce(s, CalculatorAction.Dot)
            '+' -> reduce(s, CalculatorAction.Operator(BinaryOp.ADD))
            '-' -> reduce(s, CalculatorAction.Operator(BinaryOp.SUB))
            '*' -> reduce(s, CalculatorAction.Operator(BinaryOp.MUL))
            '/' -> reduce(s, CalculatorAction.Operator(BinaryOp.DIV))
            '%' -> reduce(s, CalculatorAction.Operator(BinaryOp.MOD))
            '(' -> reduce(s, CalculatorAction.LeftParen)
            ')' -> reduce(s, CalculatorAction.RightParen)
            else -> s
        }
    }
    return reduce(s, CalculatorAction.Equals)
}

class CalculatorTest {
    @Test fun precedence() = assertEquals("7", eval("1+2*3").display)
    @Test fun parens() = assertEquals("9", eval("(1+2)*3").display)
    @Test fun nested() = assertEquals("5", eval("((2+3)*4-5)/3").display)
    @Test fun divByZero() {
        val s = eval("5/0")
        assertEquals(CalculatorError.DivisionByZero, s.error)
        assertEquals(initialState, reduce(s, CalculatorAction.ClearEntry))
    }
    @Test fun doubleDotIgnored() {
        var s = reduce(initialState, CalculatorAction.Digit('1'))
        s = reduce(s, CalculatorAction.Dot)
        s = reduce(s, CalculatorAction.Digit('2'))
        s = reduce(s, CalculatorAction.Dot)
        assertEquals("1.2", s.display)
    }
    @Test fun malformed() {
        assertEquals(CalculatorError.MalformedExpression, eval("1+").error)
        assertEquals(CalculatorError.MalformedExpression, eval("").error)
    }
    @Test fun mismatched() {
        assertEquals(CalculatorError.MismatchedParenthesis, eval("(1+2").error)
        // reduce 直接擋掉不配對的右括號，所以 "1+2)" 仍算出 3
        assertEquals("3", eval("1+2)").display)
    }
    @Test fun toggleSign() {
        var s = reduce(initialState, CalculatorAction.Digit('5'))
        s = reduce(s, CalculatorAction.ToggleSign)
        assertEquals("-5", s.display)
        s = reduce(s, CalculatorAction.ToggleSign)
        assertEquals("5", s.display)
    }
    @Test fun evaluatedThenDigitOverwrites() {
        val s = reduce(eval("2+3"), CalculatorAction.Digit('1'))
        assertEquals("1", s.display)
    }
    @Test fun backspace() {
        var s = initialState
        s = reduce(s, CalculatorAction.Digit('1'))
        s = reduce(s, CalculatorAction.Digit('2'))
        s = reduce(s, CalculatorAction.Digit('3'))
        s = reduce(s, CalculatorAction.Backspace)
        assertEquals("12", s.display)
        // 求值後按刪除視為重置（計算機常見行為）
        assertEquals(initialState, reduce(eval("123"), CalculatorAction.Backspace))
    }
    @Test fun mod() = assertEquals("1", eval("10%3").display)
    @Test fun floatFormat() = assertEquals("0.3", eval("0.1+0.2").display)
    @Test fun unaryMinus() = assertEquals("-6", eval("(-3)*2").display)
}
