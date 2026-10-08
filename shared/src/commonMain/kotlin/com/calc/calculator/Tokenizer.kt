package com.calc.calculator

internal sealed interface Token {
    data class Number(val v: Double) : Token
    data class Op(val op: BinaryOp) : Token
    data object LParen : Token
    data object RParen : Token
}

internal sealed class CalculatorException : Exception() {
    data object DivisionByZero : CalculatorException()
    data object Malformed : CalculatorException()
    data object MismatchedParenthesis : CalculatorException()
    data object Overflow : CalculatorException()
}

internal fun tokenize(expr: String): Result<List<Token>> {
    val out = mutableListOf<Token>()
    var i = 0
    var expectNumber = true
    while (i < expr.length) {
        val c = expr[i]
        when {
            c.isWhitespace() -> i++
            c == '(' -> {
                out += Token.LParen; i++; expectNumber = true
            }
            c == ')' -> {
                out += Token.RParen; i++; expectNumber = false
            }
            c.isDigit() || c == '.' || (expectNumber && (c == '+' || c == '-')) -> {
                var j = i
                if (expr[j] == '+' || expr[j] == '-') j++
                var dotSeen = false
                var digits = 0
                while (j < expr.length && (expr[j].isDigit() || expr[j] == '.')) {
                    if (expr[j] == '.') {
                        if (dotSeen) return Result.failure(CalculatorException.Malformed)
                        dotSeen = true
                    } else digits++
                    j++
                }
                if (digits == 0) return Result.failure(CalculatorException.Malformed)
                val num = expr.substring(i, j).toDoubleOrNull()
                    ?: return Result.failure(CalculatorException.Malformed)
                out += Token.Number(num)
                i = j
                expectNumber = false
            }
            !expectNumber && (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') -> {
                val op = when (c) {
                    '+' -> BinaryOp.ADD
                    '-' -> BinaryOp.SUB
                    '*' -> BinaryOp.MUL
                    '/' -> BinaryOp.DIV
                    else -> BinaryOp.MOD
                }
                out += Token.Op(op); i++; expectNumber = true
            }
            else -> return Result.failure(CalculatorException.Malformed)
        }
    }
    return Result.success(out)
}
