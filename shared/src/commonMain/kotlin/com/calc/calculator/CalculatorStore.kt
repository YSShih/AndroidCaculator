package com.calc.calculator

class CalculatorStore(initial: CalculatorState = initialState) {
    // Swift/ObjC interop does not see Kotlin default arguments,
    // so expose an explicit no-arg constructor for Swift's CalculatorStore().
    constructor() : this(initialState)

    var state: CalculatorState = initial
        private set

    fun dispatch(action: CalculatorAction) {
        state = reduce(state, action)
    }

    val display: String get() = state.display
    val expression: String get() = state.expression
}
