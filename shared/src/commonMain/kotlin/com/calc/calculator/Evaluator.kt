package com.calc.calculator

private fun precedence(op: BinaryOp): Int = when (op) {
    BinaryOp.MUL, BinaryOp.DIV, BinaryOp.MOD -> 2
    BinaryOp.ADD, BinaryOp.SUB -> 1
}

internal fun evaluate(tokens: List<Token>): Result<Double> {
    val output = mutableListOf<Token>()
    val ops = mutableListOf<Token>()
    for (t in tokens) {
        when (t) {
            is Token.Number -> output += t
            is Token.Op -> {
                while (ops.isNotEmpty()) {
                    val top = ops.last()
                    if (top is Token.Op && precedence(top.op) >= precedence(t.op)) {
                        output += ops.removeAt(ops.size - 1)
                    } else break
                }
                ops += t
            }
            is Token.LParen -> ops += t
            is Token.RParen -> {
                var found = false
                while (ops.isNotEmpty()) {
                    val top = ops.removeAt(ops.size - 1)
                    if (top is Token.LParen) {
                        found = true
                        break
                    }
                    output += top
                }
                if (!found) return Result.failure(CalculatorException.MismatchedParenthesis)
            }
        }
    }
    while (ops.isNotEmpty()) {
        val top = ops.removeAt(ops.size - 1)
        if (top is Token.LParen) return Result.failure(CalculatorException.MismatchedParenthesis)
        output += top
    }
    val stack = mutableListOf<Double>()
    for (t in output) {
        when (t) {
            is Token.Number -> stack += t.v
            is Token.Op -> {
                if (stack.size < 2) return Result.failure(CalculatorException.Malformed)
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                val r = when (t.op) {
                    BinaryOp.ADD -> a + b
                    BinaryOp.SUB -> a - b
                    BinaryOp.MUL -> a * b
                    BinaryOp.DIV -> {
                        if (b == 0.0) return Result.failure(CalculatorException.DivisionByZero)
                        a / b
                    }
                    BinaryOp.MOD -> {
                        if (b == 0.0) return Result.failure(CalculatorException.DivisionByZero)
                        a % b
                    }
                }
                if (!r.isFinite()) return Result.failure(CalculatorException.Overflow)
                stack += r
            }
            else -> return Result.failure(CalculatorException.Malformed)
        }
    }
    if (stack.size != 1) return Result.failure(CalculatorException.Malformed)
    return Result.success(stack.single())
}
