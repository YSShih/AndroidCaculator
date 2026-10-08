package com.calc.calculator

class CalculatorStore(initial: CalculatorState = initialState) {
    var state: CalculatorState = initial
        private set

    fun dispatch(action: CalculatorAction) {
        state = reduce(state, action)
    }

    val display: String get() = state.display
    val expression: String get() = state.expression
}
