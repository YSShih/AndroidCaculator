import SwiftUI
import shared

// VERIFIED_ON_MACOS：已在 macOS + Xcode 14.2 實編通過（命名以 shared.h 為準）。
// 注意：Kotlin 側嵌套類會扁平化（CalculatorAction.Digit 不存在，要用 CalculatorActionDigit）；
// Kotlin Char 對應 unichar（UInt16），數字鍵經 CalcModel.digit 轉；
// BinaryOp 條目為小寫（add/sub/mul/div/mod）。
final class CalcModel: ObservableObject {
    private let store = CalculatorStore()
    @Published private(set) var display = "0"
    @Published private(set) var expression = ""
    @Published private(set) var errorText: String? = nil

    func send(_ action: any CalculatorAction) {
        store.dispatch(action: action)
        let s = store.state
        display = s.display
        expression = s.expression
        if let e = s.error {
            errorText = String(describing: e)
        } else {
            errorText = nil
        }
    }

    func digit(_ s: String) {
        guard let v = s.unicodeScalars.first else { return }
        send(CalculatorActionDigit(d: UInt16(v.value)))
    }

    func op(_ o: BinaryOp) {
        send(CalculatorActionOperator(op: o))
    }

    func dot() { send(CalculatorActionDot()) }
    func equals() { send(CalculatorActionEquals()) }
    func leftParen() { send(CalculatorActionLeftParen()) }
    func rightParen() { send(CalculatorActionRightParen()) }
    func toggleSign() { send(CalculatorActionToggleSign()) }
    func percent() { send(CalculatorActionPercent()) }
    func backspace() { send(CalculatorActionBackspace()) }
    func clearEntry() { send(CalculatorActionClearEntry()) }
    func clearAll() { send(CalculatorActionClearAll()) }
}

struct ContentView: View {
    @StateObject private var model = CalcModel()

    private let columns: [GridItem] = Array(repeating: GridItem(.flexible(), spacing: 8), count: 4)

    var body: some View {
        VStack(spacing: 8) {
            displayView
            keypadView
        }
        .padding(12)
    }

    private var displayView: some View {
        Group {
            Spacer()
            Text(model.expression.isEmpty ? " " : model.expression)
                .frame(maxWidth: .infinity, alignment: .trailing)
                .foregroundColor(.secondary)
                .lineLimit(1)
            Text(model.errorText ?? model.display)
                .frame(maxWidth: .infinity, alignment: .trailing)
                .font(.system(size: 52))
                .foregroundColor(model.errorText == nil ? .primary : .red)
                .lineLimit(1)
        }
    }

    private var keypadView: some View {
        LazyVGrid(columns: columns, spacing: 8) {
            keypadTop
            keypadMid
            keypadBottom
        }
    }

    private var keypadTop: some View {
        Group {
            KeyButton("(", "左括號") { model.leftParen() }
            KeyButton(")", "右括號") { model.rightParen() }
            KeyButton("AC", "全部清除") { model.clearAll() }
            KeyButton("←", "刪除") { model.backspace() }
            KeyButton("C", "清除輸入") { model.clearEntry() }
            KeyButton("%", "百分比") { model.percent() }
            KeyButton("±", "正負號") { model.toggleSign() }
            KeyButton("÷", "除") { model.op(BinaryOp.div) }
        }
    }

    private var keypadMid: some View {
        Group {
            KeyButton("7", "七") { model.digit("7") }
            KeyButton("8", "八") { model.digit("8") }
            KeyButton("9", "九") { model.digit("9") }
            KeyButton("×", "乘") { model.op(BinaryOp.mul) }
            KeyButton("4", "四") { model.digit("4") }
            KeyButton("5", "五") { model.digit("5") }
            KeyButton("6", "六") { model.digit("6") }
            KeyButton("−", "減") { model.op(BinaryOp.sub) }
        }
    }

    private var keypadBottom: some View {
        Group {
            KeyButton("1", "一") { model.digit("1") }
            KeyButton("2", "二") { model.digit("2") }
            KeyButton("3", "三") { model.digit("3") }
            KeyButton("+", "加") { model.op(BinaryOp.add) }
            KeyButton("0", "零") { model.digit("0") }
            KeyButton(".", "小數點") { model.dot() }
            KeyButton("=", "等於") { model.equals() }
        }
    }
}

private struct KeyButton: View {
    let label: String
    let hint: String
    let tap: () -> Void

    init(_ label: String, _ hint: String, tap: @escaping () -> Void) {
        self.label = label
        self.hint = hint
        self.tap = tap
    }

    var body: some View {
        Button(label, action: tap)
            .font(.title2)
            .frame(maxWidth: .infinity, minHeight: 48)
            .accessibilityLabel(hint)
    }
}
