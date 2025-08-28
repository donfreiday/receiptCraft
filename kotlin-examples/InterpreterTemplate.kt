// Your team name: ___________

/**
 * Main interpreter function that processes your JSON DSL and sends commands to the printer
 * 
 * @param jsonString The JSON string containing your receipt DSL
 * @param printer The Epson printer interface (will be real hardware during judging!)
 * @param order The order data (null for Round 0)
 */
fun interpret(jsonString: String, printer: EpsonPrinter, order: Order?) {
    try {
        val jsonObject = JSONObject(jsonString)
        
        if (jsonObject.has("layout")) {
            // Parse drag & drop format
            val layout = jsonObject.getJSONObject("layout")
            val sections = layout.getJSONArray("sections")
            
            for (i in 0 until sections.length()) {
                val section = sections.getJSONObject(i)
                val type = section.getString("type")
                
                when (type) {
                    "storeHeader" -> {
                        // Set alignment
                        val alignmentStr = if (section.has("alignment")) section.getString("alignment") else "LEFT"
                        when (alignmentStr) {
                            "CENTER" -> printer.addTextAlign(Alignment.CENTER)
                            "RIGHT" -> printer.addTextAlign(Alignment.RIGHT)
                            else -> printer.addTextAlign(Alignment.LEFT)
                        }
                        
                        val content = if (section.has("content")) section.getString("content") else ""
                        printer.addText(content, TextStyle(bold = true))
                    }
                    "separator" -> {
                        printer.addText("================================", null)
                    }
                    "text" -> {
                        // Set alignment
                        val alignmentStr = if (section.has("alignment")) section.getString("alignment") else "LEFT"
                        when (alignmentStr) {
                            "CENTER" -> printer.addTextAlign(Alignment.CENTER)
                            "RIGHT" -> printer.addTextAlign(Alignment.RIGHT)
                            else -> printer.addTextAlign(Alignment.LEFT)
                        }
                        
                        val content = if (section.has("content")) section.getString("content") else ""
                        printer.addText(content, null)
                    }
                    "orderInfo", "item", "itemPrice", "subtotal", "tax", "total" -> {
                        // Set alignment
                        val alignmentStr = if (section.has("alignment")) section.getString("alignment") else "LEFT"
                        when (alignmentStr) {
                            "CENTER" -> printer.addTextAlign(Alignment.CENTER)
                            "RIGHT" -> printer.addTextAlign(Alignment.RIGHT)
                            else -> printer.addTextAlign(Alignment.LEFT)
                        }
                        
                        val content = if (section.has("content")) section.getString("content") else ""
                        val isBold = type == "total"
                        if (isBold) {
                            printer.addText(content, TextStyle(bold = true))
                        } else {
                            printer.addText(content, null)
                        }
                    }
                    "itemHeader" -> {
                        // Set alignment
                        val alignmentStr = if (section.has("alignment")) section.getString("alignment") else "LEFT"
                        when (alignmentStr) {
                            "CENTER" -> printer.addTextAlign(Alignment.CENTER)
                            "RIGHT" -> printer.addTextAlign(Alignment.RIGHT)
                            else -> printer.addTextAlign(Alignment.LEFT)
                        }
                        
                        val content = if (section.has("content")) section.getString("content") else ""
                        printer.addText(content, null)
                    }
                    "thankYou" -> {
                        // Set alignment
                        val alignmentStr = if (section.has("alignment")) section.getString("alignment") else "CENTER"
                        when (alignmentStr) {
                            "CENTER" -> printer.addTextAlign(Alignment.CENTER)
                            "RIGHT" -> printer.addTextAlign(Alignment.RIGHT)
                            else -> printer.addTextAlign(Alignment.LEFT)
                        }
                        
                        val content = if (section.has("content")) section.getString("content") else "Thank you!"
                        printer.addText(content, null)
                    }
                    "spacer" -> {
                        val lines = if (section.has("lines")) section.getInt("lines") else 1
                        printer.addFeedLine(lines)
                    }
                }
            }
        } else {
            // Simple fallback for non-layout JSON
            printer.addTextAlign(Alignment.CENTER)
            printer.addText("No layout found in JSON", null)
        }
        
        printer.addFeedLine(2)
        printer.cutPaper()
        
    } catch (e: Exception) {
        printer.addTextAlign(Alignment.CENTER)
        printer.addText("ERROR: " + e.message, TextStyle(bold = true))
        printer.addFeedLine(2)
        printer.cutPaper()
    }
}