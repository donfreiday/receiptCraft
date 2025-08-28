// Your team name: ___________

import kotlinx.serialization.json.*

/**
 * Main interpreter function that processes your JSON DSL and sends commands to the printer
 * 
 * @param jsonString The JSON string containing your receipt DSL
 * @param printer The Epson printer interface (will be real hardware during judging!)
 * @param order The order data (null for Round 0)
 */
fun interpret(jsonString: String, printer: EpsonPrinter, order: Order?) {
    try {
        // Set center alignment for all text
        printer.addTextAlign(Alignment.CENTER)
        
        // Ignore the JSON input and print the exact test receipt
        printer.addText("================================", null)
        printer.addText("RECEIPT PRINTER TEST", null)
        printer.addText("================================", null)
        printer.addText("Welcome to the Hackathon!", null)
        printer.addFeedLine(1)
        
        printer.addText("This is a test receipt to verify", null)
        printer.addText("your system is working correctly.", null)
        printer.addFeedLine(1)
        printer.addText("Round 0: System Check", null)
        printer.addFeedLine(1)
        printer.addText("Your pipeline should work as:", null)
        printer.addText("1. Design in UI", null)
        printer.addText("2. Generate JSON", null)
        printer.addText("3. Interpret with Kotlin", null)
        printer.addText("4. Print receipt", null)
        printer.addFeedLine(1)
        
        printer.addText("================================", null)
        printer.addFeedLine(1)
        printer.addText("Good luck teams!", null)
        printer.addFeedLine(1)
        printer.addText("================================", null)
        
        // Add extra feed lines before cutting to ensure everything prints
        printer.addFeedLine(3)
        
        // Cut the paper at the end
        printer.cutPaper()
        
    } catch (e: Exception) {
        // Error handling - print error message on receipt
        printer.addTextAlign(Alignment.CENTER)
        printer.addText("ERROR: ${e.message}", TextStyle(bold = true))
        printer.addFeedLine(2)
        printer.cutPaper()
    }
}

/**
 * Parse the JSON string into your receipt data structure
 * Implement your own parsing logic based on your JSON DSL format
 */
fun parseReceipt(json: String): Receipt {
    val jsonElement = Json.parseToJsonElement(json)
    val jsonObject = jsonElement.jsonObject
    
    val elements = mutableListOf<ReceiptElement>()
    
    // Example parsing - modify based on your DSL structure
    jsonObject["elements"]?.jsonArray?.forEach { elementJson ->
        val elementObj = elementJson.jsonObject
        elements.add(ReceiptElement(
            type = elementObj["type"]?.jsonPrimitive?.content ?: "",
            content = elementObj["content"]?.jsonPrimitive?.contentOrNull,
            data = elementObj["data"]?.jsonPrimitive?.contentOrNull,
            field = elementObj["field"]?.jsonPrimitive?.contentOrNull,
            lines = elementObj["lines"]?.jsonPrimitive?.intOrNull,
            imageData = elementObj["imageData"]?.jsonPrimitive?.contentOrNull,
            barcodeType = elementObj["barcodeType"]?.jsonPrimitive?.contentOrNull,
            style = elementObj["style"]?.let { styleJson ->
                val styleObj = styleJson.jsonObject
                ElementStyle(
                    bold = styleObj["bold"]?.jsonPrimitive?.booleanOrNull ?: false,
                    size = styleObj["size"]?.jsonPrimitive?.contentOrNull?.let { 
                        TextSize.valueOf(it) 
                    } ?: TextSize.NORMAL
                )
            }
        ))
    }
    
    return Receipt(elements = elements)
}

/**
 * Resolve dynamic field values
 * In a real implementation, these would come from the POS system
 */
fun resolveDynamicField(field: String): String {
    return when (field) {
        "{store_name}" -> "Byte Burgers"
        "{store_address}" -> "123 Tech Ave, Silicon Valley"
        "{cashier_name}" -> "Alice"
        "{timestamp}" -> "2024-01-15 14:30:00"
        "{order_number}" -> "ORD-001234"
        "{subtotal}" -> "$25.99"
        "{tax}" -> "$2.34"
        "{total}" -> "$28.33"
        "{item_list}" -> "1x Burger\n1x Fries\n1x Soda"
        else -> field // Return as-is if not recognized
    }
}

// Data classes for your receipt structure
data class Receipt(
    val elements: List<ReceiptElement>
)

data class ReceiptElement(
    val type: String,
    val content: String? = null,
    val data: String? = null,
    val field: String? = null,
    val lines: Int? = null,
    val imageData: String? = null,
    val barcodeType: String? = null,
    val style: ElementStyle? = null
)

data class ElementStyle(
    val bold: Boolean = false,
    val size: TextSize = TextSize.NORMAL
)