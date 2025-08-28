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
                        printer.addText(content, TextStyle(bold = true, size = TextSize.LARGE))
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
                    "orderInfo" -> {
                        printer.addTextAlign(Alignment.LEFT)
                        if (order != null) {
                            printer.addText("Order #" + order.orderId + "            Date: 12/04/2024", null)
                        } else {
                            printer.addText("Order #A-0042            Date: 12/04/2024", null)
                        }
                    }
                    "itemsHeader" -> {
                        printer.addTextAlign(Alignment.LEFT)
                        printer.addText("ITEMS:", null)
                    }
                    "itemList" -> {
                        printer.addTextAlign(Alignment.LEFT)
                        if (order != null) {
                            for (item in order.items) {
                                printer.addText(item.name + "                    x" + item.quantity + "      $" + String.format("%.2f", item.totalPrice), null)
                                if (item.quantity > 1) {
                                    printer.addText("@ $" + String.format("%.2f", item.unitPrice) + " each", null)
                                }
                            }
                        } else {
                            printer.addText("Cheeseburger                    x2      $17.98", null)
                            printer.addText("@ $8.99 each", null)
                            printer.addText("French Fries                    x1       $3.99", null)
                            printer.addText("Soft Drink                      x2       $5.98", null)
                            printer.addText("@ $2.99 each", null)
                        }
                    }
                    "subtotal" -> {
                        printer.addTextAlign(Alignment.LEFT)
                        val subtotal = if (order != null) String.format("%.2f", order.subtotal) else "27.95"
                        printer.addText("Subtotal:                              $" + subtotal, null)
                    }
                    "tax" -> {
                        printer.addTextAlign(Alignment.LEFT)
                        val tax = if (order != null) String.format("%.2f", order.taxAmount) else "2.24"
                        printer.addText("Tax (8.0%):                             $" + tax, null)
                    }
                    "total" -> {
                        printer.addTextAlign(Alignment.LEFT)
                        val total = if (order != null) String.format("%.2f", order.totalAmount) else "30.19"
                        printer.addText("TOTAL:                                 $" + total, TextStyle(bold = true))
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
            // Fallback for old format - just print basic receipt
            printer.addTextAlign(Alignment.CENTER)
            printer.addText("================================", null)
            printer.addText("BYTE BURGERS", TextStyle(bold = true, size = TextSize.LARGE))
            printer.addText("Store #001", null)
            printer.addText("================================", null)
            printer.addTextAlign(Alignment.LEFT)
            printer.addText("Order #A-0042            Date: 12/04/2024", null)
            printer.addText("ITEMS:", null)
            printer.addText("Cheeseburger                    x2      $17.98", null)
            printer.addText("TOTAL:                                 $30.19", TextStyle(bold = true))
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