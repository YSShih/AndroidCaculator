package com.calc.calculator

import kotlin.math.roundToLong

internal fun formatDisplay(v: Double): String {
    if (!v.isFinite()) throw CalculatorException.Overflow
    if (v == 0.0) return "0"
    val rounded = (v * 1e10).roundToLong() / 1e10
    val l = rounded.roundToLong()
    if (rounded == l.toDouble() && l in -999999999999999L..999999999999999L) return l.toString()
    var s = rounded.toString()
    if ('.' in s) {
        s = s.trimEnd('0').trimEnd('.')
    }
    return s.ifEmpty { "0" }
}
