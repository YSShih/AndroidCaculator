import SwiftUI
import shared

// UNVERIFIED_ON_WINDOWS：在 Windows 無法編譯，需 macOS + Xcode 執行
// `./gradlew :shared:embedAndSignAppleFrameworkForXcode` 後接線驗證。
final class CalcModel: ObservableObject {
    private let store = CalculatorStore()
    @Published private(set) var display = "0"
    @Published private(set) var expression = ""
    @Published private(set) var errorText: String? = nil

    func send(_ action: CalculatorAction) {
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
}

struct ContentView: View {
    @StateObject private var model = CalcModel()

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 8), count: 4)

    var body: some View {
        VStack(spacing: 8) {
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
            LazyVGrid(columns: columns, spacing: 8) {
                Group {
                    KeyButton("(", "左括號") { model.send(CalculatorAction.LeftParen()) }
                    KeyButton(")", "右括號") { model.send(CalculatorAction.RightParen()) }
                    KeyButton("AC", "全部清除") { model.send(CalculatorAction.ClearAll()) }
                    KeyButton("←", "刪除") { model.send(CalculatorAction.Backspace()) }
                    KeyButton("C", "清除輸入") { model.send(CalculatorAction.ClearEntry()) }
                    KeyButton("%", "百分比") { model.send(CalculatorAction.Percent()) }
                    KeyButton("±", "正負號") { model.send(CalculatorAction.ToggleSign()) }
                    KeyButton("÷", "除") { model.send(CalculatorAction.Operator(op: BinaryOp.div)) }
                    KeyButton("7", "七") { model.send(CalculatorAction.Digit(d: "7")) }
                    KeyButton("8", "八") { model.send(CalculatorAction.Digit(d: "8")) }
                    KeyButton("9", "九") { model.send(CalculatorAction.Digit(d: "9")) }
                    KeyButton("×", "乘") { model.send(CalculatorAction.Operator(op: BinaryOp.mul)) }
                    KeyButton("4", "四") { model.send(CalculatorAction.Digit(d: "4")) }
                    KeyButton("5", "五") { model.send(CalculatorAction.Digit(d: "5")) }
                    KeyButton("6", "六") { model.send(CalculatorAction.Digit(d: "6")) }
                    KeyButton("−", "減") { model.send(CalculatorAction.Operator(op: BinaryOp.sub)) }
                    KeyButton("1", "一") { model.send(CalculatorAction.Digit(d: "1")) }
                    KeyButton("2", "二") { model.send(CalculatorAction.Digit(d: "2")) }
                    KeyButton("3", "三") { model.send(CalculatorAction.Digit(d: "3")) }
                    KeyButton("+", "加") { model.send(CalculatorAction.Operator(op: BinaryOp.add)) }
                    KeyButton("0", "零") { model.send(CalculatorAction.Digit(d: "0")) }
                    KeyButton(".", "小數點") { model.send(CalculatorAction.Dot()) }
                    KeyButton("=", "等於") { model.send(CalculatorAction.Equals()) }
                }
            }
        }
        .padding(12)
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
