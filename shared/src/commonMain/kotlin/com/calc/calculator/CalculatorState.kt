package com.calc.calculator

enum class BinaryOp { ADD, SUB, MUL, DIV, MOD }

sealed interface CalculatorAction {
    data class Digit(val d: Char) : CalculatorAction
    data object Dot : CalculatorAction
    data class Operator(val op: BinaryOp) : CalculatorAction
    data object LeftParen : CalculatorAction
    data object RightParen : CalculatorAction
    data object ToggleSign : CalculatorAction
    data object Percent : CalculatorAction
    data object Backspace : CalculatorAction
    data object ClearEntry : CalculatorAction
    data object ClearAll : CalculatorAction
    data object Equals : CalculatorAction
}

sealed interface CalculatorError {
    data object DivisionByZero : CalculatorError
    data object MalformedExpression : CalculatorError
    data object MismatchedParenthesis : CalculatorError
    data object NumericOverflow : CalculatorError
}

data class CalculatorState(
    val expression: String = "",
    val display: String = "0",
    val error: CalculatorError? = null,
    val justEvaluated: Boolean = false,
)
