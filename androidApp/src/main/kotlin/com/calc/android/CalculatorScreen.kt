package com.calc.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calc.calculator.BinaryOp
import com.calc.calculator.CalculatorAction
import com.calc.calculator.CalculatorState

private data class Key(
    val label: String,
    val description: String,
    val action: CalculatorAction,
    val accent: Boolean = false,
    val span: Float = 1f,
)

private val rows: List<List<Key>> = listOf(
    listOf(
        Key("(", "左括號", CalculatorAction.LeftParen),
        Key(")", "右括號", CalculatorAction.RightParen),
        Key("AC", "全部清除", CalculatorAction.ClearAll),
        Key("⌫", "刪除", CalculatorAction.Backspace),
    ),
    listOf(
        Key("C", "清除輸入", CalculatorAction.ClearEntry),
        Key("%", "百分比", CalculatorAction.Percent),
        Key("±", "正負號", CalculatorAction.ToggleSign),
        Key("÷", "除", CalculatorAction.Operator(BinaryOp.DIV), accent = true),
    ),
    listOf(
        Key("7", "七", CalculatorAction.Digit('7')),
        Key("8", "八", CalculatorAction.Digit('8')),
        Key("9", "九", CalculatorAction.Digit('9')),
        Key("×", "乘", CalculatorAction.Operator(BinaryOp.MUL), accent = true),
    ),
    listOf(
        Key("4", "四", CalculatorAction.Digit('4')),
        Key("5", "五", CalculatorAction.Digit('5')),
        Key("6", "六", CalculatorAction.Digit('6')),
        Key("−", "減", CalculatorAction.Operator(BinaryOp.SUB), accent = true),
    ),
    listOf(
        Key("1", "一", CalculatorAction.Digit('1')),
        Key("2", "二", CalculatorAction.Digit('2')),
        Key("3", "三", CalculatorAction.Digit('3')),
        Key("+", "加", CalculatorAction.Operator(BinaryOp.ADD), accent = true),
    ),
    listOf(
        Key("0", "零", CalculatorAction.Digit('0')),
        Key(".", "小數點", CalculatorAction.Dot),
        Key("=", "等於", CalculatorAction.Equals, accent = true, span = 2f),
    ),
)

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val compact = maxWidth < 360.dp
        val displaySp = when {
            state.display.length > 12 -> 28.sp
            state.display.length > 9 -> 36.sp
            compact -> 44.sp
            else -> 52.sp
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = state.expression.ifEmpty { " " },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = state.error?.let { errorName(it) } ?: state.display,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                fontSize = displaySp,
                color = if (state.error != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { key ->
                        CalcButton(
                            key = key,
                            onAction = onAction,
                            modifier = Modifier
                                .weight(key.span)
                                .padding(vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalcButton(
    key: Key,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onAction(key.action) },
        colors = if (key.accent) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        } else ButtonDefaults.buttonColors(),
        modifier = modifier
            .sizeIn(minHeight = 48.dp)
            .semantics { contentDescription = key.description },
    ) {
        Text(text = key.label, fontSize = 22.sp, maxLines = 1)
    }
}

private fun errorName(error: com.calc.calculator.CalculatorError): String = when (error) {
    is com.calc.calculator.CalculatorError.DivisionByZero -> "錯誤：除以零"
    is com.calc.calculator.CalculatorError.MalformedExpression -> "錯誤：算式不完整"
    is com.calc.calculator.CalculatorError.MismatchedParenthesis -> "錯誤：括號不配對"
    is com.calc.calculator.CalculatorError.NumericOverflow -> "錯誤：數值溢位"
}
