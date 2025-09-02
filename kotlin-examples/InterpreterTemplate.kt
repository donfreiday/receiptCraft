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
                    "header", "storeHeader" -> {
                        // Set alignment
                        val alignmentStr = if (section.has("alignment")) section.getString("alignment") else "LEFT"
                        when (alignmentStr) {
                            "CENTER" -> printer.addTextAlign(Alignment.CENTER)
                            "RIGHT" -> printer.addTextAlign(Alignment.RIGHT)
                            else -> printer.addTextAlign(Alignment.LEFT)
                        }
                        
                        val content = if (section.has("content")) section.getString("content") else ""
                        printer.addText(content, TextStyle(bold = true))
                        printer.addFeedLine(1)
                    }
                    "separator" -> {
                        val content = if (section.has("content")) section.getString("content") else "================================"
                        printer.addText(content, null)
                        printer.addFeedLine(1)
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
                        printer.addFeedLine(1)
                    }
                    "orderInfo", "item", "itemPrice", "itemPromotion", "subtotal", "discounts", "promotion", "subtotalAfter", "tax", "total", "loyalty", "customerName", "memberStatus", "customerId", "loyaltyPoints", "memberSince", "paymentMethod", "pointsEarned", "totalPoints", "membershipStatus", "customerProfile", "itemSubtotal", "itemDiscounts", "memberAppreciation", "appDiscount", "subtotalAfterAll", "paymentType", "pointsEarnedToday", "newBalance", "nextReward", "personalizedThanks", "tableInfo", "guestInfo", "groupId", "itemModifier", "payerName", "payingFor", "payerItem", "tip", "payerTotal", "totalTips", "grandTotal", "vipThanks" -> {
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
                        printer.addFeedLine(1)
                    }
                    "itemHeader", "promotionHeader", "customerHeader", "loyaltyRewardsHeader", "platinumMember", "orderItemsHeader", "categoryHeader", "orderPromotionHeader", "rewardsSummary", "vipGroup", "orderDetails", "entreeHeader", "appetizerHeader", "dessertHeader", "beverageHeader", "splitPaymentHeader", "paymentSummaryHeader" -> {
                        // Set alignment
                        val alignmentStr = if (section.has("alignment")) section.getString("alignment") else "LEFT"
                        when (alignmentStr) {
                            "CENTER" -> printer.addTextAlign(Alignment.CENTER)
                            "RIGHT" -> printer.addTextAlign(Alignment.RIGHT)
                            else -> printer.addTextAlign(Alignment.LEFT)
                        }
                        
                        val content = if (section.has("content")) section.getString("content") else ""
                        printer.addText(content, null)
                        printer.addFeedLine(1)
                    }
                    "thankYou", "thanks" -> {
                        // Set alignment
                        val alignmentStr = if (section.has("alignment")) section.getString("alignment") else "CENTER"
                        when (alignmentStr) {
                            "CENTER" -> printer.addTextAlign(Alignment.CENTER)
                            "RIGHT" -> printer.addTextAlign(Alignment.RIGHT)
                            else -> printer.addTextAlign(Alignment.LEFT)
                        }
                        
                        val content = if (section.has("content")) section.getString("content") else "Thank you!"
                        val lines = content.split("\n")
                        for (line in lines) {
                            printer.addText(line, null)
                            printer.addFeedLine(1)
                        }
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