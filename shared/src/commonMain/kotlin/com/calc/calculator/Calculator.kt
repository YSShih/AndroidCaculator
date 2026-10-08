package com.calc.calculator

val initialState: CalculatorState = CalculatorState()

fun opSymbol(op: BinaryOp): Char = when (op) {
    BinaryOp.ADD -> '+'
    BinaryOp.SUB -> '-'
    BinaryOp.MUL -> '*'
    BinaryOp.DIV -> '/'
    BinaryOp.MOD -> '%'
}

private fun isOpChar(c: Char): Boolean = c == '+' || c == '-' || c == '*' || c == '/' || c == '%'

private fun currentNumber(expr: String): String {
    if (expr.isEmpty()) return ""
    var i = expr.length - 1
    while (i >= 0 && (expr[i].isDigit() || expr[i] == '.')) i--
    // include unary minus
    if (i >= 0 && expr[i] == '-' && (i == 0 || isOpChar(expr[i - 1]) || expr[i - 1] == '(')) i--
    return expr.substring(i + 1)
}

private fun replaceCurrentNumber(expr: String, replacement: String): String {
    if (expr.isEmpty()) return replacement
    var i = expr.length - 1
    while (i >= 0 && (expr[i].isDigit() || expr[i] == '.')) i--
    if (i >= 0 && expr[i] == '-' && (i == 0 || isOpChar(expr[i - 1]) || expr[i - 1] == '(')) i--
    return expr.substring(0, i + 1) + replacement
}

fun reduce(state: CalculatorState, action: CalculatorAction): CalculatorState {
    if (state.error != null) {
        return when (action) {
            is CalculatorAction.ClearAll,
            is CalculatorAction.ClearEntry -> initialState
            is CalculatorAction.Digit -> reduce(initialState, action)
            else -> state
        }
    }
    return when (action) {
        is CalculatorAction.Digit -> {
            require(action.d in '0'..'9')
            if (state.justEvaluated) {
                CalculatorState(expression = action.d.toString(), display = action.d.toString())
            } else {
                val cur = currentNumber(state.expression)
                if (cur == "0" && action.d == '0') return state
                val newExpr = if (cur == "0") replaceCurrentNumber(state.expression, action.d.toString())
                else if (state.expression == "0") action.d.toString()
                else state.expression + action.d
                state.copy(expression = newExpr, display = currentNumber(newExpr).ifEmpty { state.display })
            }
        }
        is CalculatorAction.Dot -> {
            if (state.justEvaluated) {
                CalculatorState(expression = "0.", display = "0.")
            } else {
                val cur = currentNumber(state.expression)
                if ('.' in cur) return state
                val newExpr = if (cur.isEmpty()) state.expression + "0." else state.expression + "."
                state.copy(expression = newExpr, display = currentNumber(newExpr))
            }
        }
        is CalculatorAction.Operator -> {
            val sym = opSymbol(action.op)
            if (state.justEvaluated) {
                CalculatorState(expression = state.display + sym, display = state.display)
            } else if (state.expression.isEmpty()) {
                if (action.op == BinaryOp.SUB) state.copy(expression = "-", display = "-")
                else state
            } else {
                val last = state.expression.last()
                val newExpr = if (isOpChar(last)) state.expression.dropLast(1) + sym else state.expression + sym
                state.copy(expression = newExpr)
            }
        }
        is CalculatorAction.LeftParen -> {
            if (state.justEvaluated) CalculatorState(expression = "(", display = "0")
            else state.copy(expression = state.expression + "(")
        }
        is CalculatorAction.RightParen -> {
            val open = state.expression.count { it == '(' }
            val close = state.expression.count { it == ')' }
            if (open > close && state.expression.isNotEmpty() && (state.expression.last().isDigit() || state.expression.last() == ')')) {
                state.copy(expression = state.expression + ")")
            } else state
        }
        is CalculatorAction.ToggleSign -> {
            if (state.justEvaluated) {
                val v = state.display.toDoubleOrNull() ?: return state
                val neg = formatDisplay(-v)
                CalculatorState(expression = neg, display = neg)
            } else {
                val cur = currentNumber(state.expression)
                if (cur.isEmpty() || cur == "0" || cur == "0.") return state
                val negated = if (cur.startsWith("-")) cur.drop(1) else "-$cur"
                val newExpr = replaceCurrentNumber(state.expression, negated)
                state.copy(expression = newExpr, display = negated)
            }
        }
        is CalculatorAction.Percent -> {
            val cur = if (state.justEvaluated) state.display else currentNumber(state.expression)
            val v = cur.toDoubleOrNull() ?: return state
            val pct = formatDisplay(v / 100.0)
            if (state.justEvaluated) CalculatorState(expression = pct, display = pct)
            else state.copy(expression = replaceCurrentNumber(state.expression, pct), display = pct)
        }
        is CalculatorAction.Backspace -> {
            if (state.justEvaluated) initialState
            else if (state.expression.isEmpty()) state
            else {
                val newExpr = state.expression.dropLast(1)
                if (newExpr.isEmpty()) initialState
                else state.copy(expression = newExpr, display = currentNumber(newExpr).ifEmpty { "0" })
            }
        }
        is CalculatorAction.ClearEntry -> {
            if (state.justEvaluated) initialState
            else {
                val cur = currentNumber(state.expression)
                if (cur.isEmpty()) state
                else {
                    val newExpr = replaceCurrentNumber(state.expression, "")
                    if (newExpr.isEmpty()) initialState
                    else state.copy(expression = newExpr, display = "0")
                }
            }
        }
        is CalculatorAction.ClearAll -> initialState
        is CalculatorAction.Equals -> {
            if (state.expression.isBlank()) {
                state.copy(error = CalculatorError.MalformedExpression)
            } else {
                val tokens = tokenize(state.expression)
                val result = tokens.fold(
                    onSuccess = { evaluate(it) },
                    onFailure = {
                        Result.failure(it as? CalculatorException ?: CalculatorException.Malformed)
                    }
                )
                result.fold(
                    onSuccess = { v ->
                        try {
                            val text = formatDisplay(v)
                            CalculatorState(expression = text, display = text, justEvaluated = true)
                        } catch (e: CalculatorException) {
                            state.copy(error = CalculatorError.NumericOverflow)
                        }
                    },
                    onFailure = { e ->
                        val err = when (e as? CalculatorException) {
                            is CalculatorException.DivisionByZero -> CalculatorError.DivisionByZero
                            is CalculatorException.MismatchedParenthesis -> CalculatorError.MismatchedParenthesis
                            is CalculatorException.Overflow -> CalculatorError.NumericOverflow
                            else -> CalculatorError.MalformedExpression
                        }
                        state.copy(error = err)
                    }
                )
            }
        }
    }
}
