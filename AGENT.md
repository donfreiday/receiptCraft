Visual Receipt Designer (JavaScript/React):
Build a drag-and-drop interface for designing receipts
Support elements like text blocks, headers, item lists, totals, QR codes, etc.
Generate a JSON DSL that describes the receipt LAYOUT (not data)
Must call onJsonUpdate(jsonString) when design changes

Kotlin Interpreter Function:
Parse your JSON DSL and Order data
Convert them into printer commands using the EpsonPrinter interface
Handle dynamic data from different competition rounds

Data Flow:
Design: Create visual receipt layout → Generate JSON DSL
Submit: JSON + Kotlin Code → Compilation Server
Execute: Server compiles & runs your interpreter with Order data
Print: ESC/POS commands → Android → Physical receipt

---

🖥️ FRONTEND (This App)

JSON

HTTP POST
{json, kotlin, order}

ESC/POS Commands

Prints

🤖 ANDROID PRINT SERVER

Receives printer commands
Sends to physical Epson printer

⚙️ COMPILATION SERVER

Compiles Kotlin code
Executes interpret() function
Passes JSON + Order
Captures printer commands

📐 Design Tab
Drag & Drop UI
Generates JSON DSL

📤 Submit Tab
Kotlin Code Editor
interpret() function

🧾 Physical Receipt!

---

Here is an example of something sort of like the structure we want

package dev.yum.poseidon.services.printingservice.models

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

abstract class PrintableReceipt {

    abstract val layout: ReceiptLayout

    abstract val printableReceiptType: PrintableReceiptType

    fun build(builder: ReceiptLayout.() -> Unit): ReceiptLayout {
        return ReceiptLayout().apply(builder)
    }

    fun toSerializableReceipt() =
        SerializableReceipt(
            receiptType = printableReceiptType,
            receiptLines = layout.printableLines.toSerializableReceiptLines(),
        )

    class ReceiptLayout(
        private val overridePrintableLines: List<ReceiptLine>? = null,
    ) {
        private val sections = mutableListOf<ReceiptSection>()

        val printableLines: List<ReceiptLine>
            get() = overridePrintableLines ?: sections.flatMap { it.template.lines }

        fun section(section: ReceiptSection) {
            if (section.shouldRender) {
                sections.add(section)
            }
        }

        fun append(section: ReceiptSection): ReceiptLayout {
            if (section.shouldRender) {
                sections.add(section)
            }
            return this
        }
    }

}

```kotlin
@JsonClass(generateAdapter = true)
@Serializable
data class SerializableReceipt(
    val receiptLines: List<SerializableReceiptLine>,
    val receiptType: PrintableReceiptType,
) {
    fun toPrintableReceipt(): PrintableReceipt {
        val serializableReceiptLines = receiptLines.map {
            when (it.type) {
                ReceiptLineType.TEXT -> TextLine(it.value, it.modifier)
                ReceiptLineType.IMAGE -> ImageLine(it.value.toInt(), it.modifier)
                ReceiptLineType.BARCODE -> BarCodeLine(it.value, it.modifier)
            }
        }
        return object : PrintableReceipt() {
            override val layout = ReceiptLayout(serializableReceiptLines)
            override val printableReceiptType: PrintableReceiptType = receiptType
        }
    }
}

@JsonClass(generateAdapter = true)
@Serializable
data class SerializableReceiptLine(
    val value: String,
    val type: ReceiptLineType,
    val modifier: PrintModifier = PrintModifier(),
)

enum class ReceiptLineType {
    TEXT,
    IMAGE,
    BARCODE,
}

enum class PrintableReceiptType {
    SALES,
    BUMP,
    REPORT,
}

fun List<ReceiptLine>.toSerializableReceiptLines(): List<SerializableReceiptLine> {
    return map { line ->
        val (lineType, lineValue) = when (line) {
            is TextLine -> ReceiptLineType.TEXT to line.text
            is ImageLine -> ReceiptLineType.IMAGE to line.androidImageResourceId.toString()
            is BarCodeLine -> ReceiptLineType.BARCODE to line.barCode
            else -> throw IllegalArgumentException("Unknown line type")
        }
        SerializableReceiptLine(
            value = lineValue,
            type = lineType,
            modifier = line.modifier,
        )
    }
}
```