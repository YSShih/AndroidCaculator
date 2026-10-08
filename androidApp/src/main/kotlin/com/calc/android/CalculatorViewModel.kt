package com.calc.android

import androidx.lifecycle.ViewModel
import com.calc.calculator.CalculatorAction
import com.calc.calculator.CalculatorState
import com.calc.calculator.initialState
import com.calc.calculator.reduce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        _state.update { reduce(it, action) }
    }
}
