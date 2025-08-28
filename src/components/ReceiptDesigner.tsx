'use client';

import React, { useState, useCallback } from 'react';

interface ReceiptDesignerProps {
  onJsonUpdate: (json: string) => void;
}

interface ReceiptElement {
  id: string;
  type: string;
  content?: string;
  alignment?: string;
  style?: {
    bold?: boolean;
    size?: string;
  };
  lines?: number;
}

interface DragItem {
  type: string;
  label: string;
  defaultContent?: string;
  alignment?: string;
}

const ELEMENT_TYPES: DragItem[] = [
  { type: 'header', label: '🏪 Store Header', defaultContent: 'BYTE BURGERS', alignment: 'CENTER' },
  { type: 'separator', label: '═══ Separator', defaultContent: '================================================' },
  { type: 'text', label: '📝 Text Line', defaultContent: 'Store #001' },
  { type: 'orderInfo', label: '📋 Order Info', defaultContent: 'Order #A-0042' },
  { type: 'itemHeader', label: '🛍️ Items Section', defaultContent: 'ITEMS:' },
  { type: 'item', label: '🍔 Menu Item', defaultContent: 'Cheeseburger' },
  { type: 'itemPrice', label: '💰 Item with Price', defaultContent: 'French Fries' },
  { type: 'itemPromotion', label: '🎯 Item Promotion', defaultContent: '  ** Buy One Get One 50% Off      -$3.00 **' },
  { type: 'subtotal', label: '🧮 Subtotal', defaultContent: 'Subtotal:' },
  { type: 'discounts', label: '💸 Discounts', defaultContent: 'Discounts:' },
  { type: 'promotionHeader', label: '🎁 Promotion Header', defaultContent: 'PROMOTIONS:' },
  { type: 'promotion', label: '🎉 Order Promotion', defaultContent: '  Morning Rush Special                 -$2.00' },
  { type: 'subtotalAfter', label: '🧾 Subtotal After', defaultContent: 'Subtotal after discounts:' },
  { type: 'tax', label: '🏛️ Tax Line', defaultContent: 'Tax (8.0%):' },
  { type: 'total', label: '💳 Total', defaultContent: 'TOTAL:' },
  { type: 'customerHeader', label: '👤 Customer Header', defaultContent: 'CUSTOMER INFORMATION:' },
  { type: 'customerName', label: '📛 Customer Name', defaultContent: '  Name: John Doe' },
  { type: 'memberStatus', label: '🏆 Member Status', defaultContent: '  Member Status: GOLD' },
  { type: 'customerId', label: '🆔 Customer ID', defaultContent: '  Customer ID: CUST-8826' },
  { type: 'loyaltyPoints', label: '💎 Loyalty Points', defaultContent: '  Loyalty Points: 1,247' },
  { type: 'memberSince', label: '📅 Member Since', defaultContent: '  Member Since: 2019-03-15' },
  { type: 'paymentMethod', label: '💳 Payment Method', defaultContent: 'Payment Method: VISA ****1234' },
  { type: 'loyaltyRewardsHeader', label: '🎁 Loyalty Rewards', defaultContent: 'LOYALTY REWARDS:' },
  { type: 'pointsEarned', label: '⭐ Points Earned', defaultContent: '  Points Earned Today: 40' },
  { type: 'totalPoints', label: '💰 Total Points', defaultContent: '  Total Points: 1,287' },
  { type: 'membershipStatus', label: '👑 Membership Status', defaultContent: '  Status: GOLD Member' },
  { type: 'loyalty', label: '⭐ Loyalty Points', defaultContent: '         Loyalty points earned: 27' },
  { type: 'thanks', label: '😊 Thank You', defaultContent: 'Thank you for your order!' },
  { type: 'spacer', label: '⬜ Blank Line', lines: 1 },
];

export const ReceiptDesigner: React.FC<ReceiptDesignerProps> = ({ onJsonUpdate }) => {
  const [elements, setElements] = useState<ReceiptElement[]>([]);
  const [draggedItem, setDraggedItem] = useState<DragItem | null>(null);
  const [selectedElement, setSelectedElement] = useState<string | null>(null);

  const generateId = () => Math.random().toString(36).substr(2, 9);

  const updateJson = useCallback((elementList: ReceiptElement[]) => {
    const receiptJson = {
      layout: {
        alignment: "CENTER",
        sections: elementList.map(el => ({
          type: el.type,
          content: el.content,
          alignment: el.alignment || 'LEFT',
          lines: el.lines
        }))
      }
    };
    onJsonUpdate(JSON.stringify(receiptJson, null, 2));
  }, [onJsonUpdate]);

  // Load initial state or persist across tab changes
  React.useEffect(() => {
    if (elements.length === 0) {
      // Initialize with empty state - user can load template
      updateJson([]);
    }
  }, [updateJson]);

  const addElement = (dragItem: DragItem, index?: number) => {
    const newElement: ReceiptElement = {
      id: generateId(),
      type: dragItem.type,
      content: dragItem.defaultContent,
      alignment: dragItem.alignment,
      lines: dragItem.type === 'spacer' ? 1 : undefined,
    };

    setElements(prev => {
      let newElements;
      if (index !== undefined) {
        newElements = [...prev];
        newElements.splice(index, 0, newElement);
      } else {
        newElements = [...prev, newElement];
      }
      updateJson(newElements);
      return newElements;
    });
  };

  const removeElement = (id: string) => {
    setElements(prev => {
      const newElements = prev.filter(el => el.id !== id);
      updateJson(newElements);
      return newElements;
    });
  };

  const updateElement = (id: string, updates: Partial<ReceiptElement>) => {
    setElements(prev => {
      const newElements = prev.map(el => 
        el.id === id ? { ...el, ...updates } : el
      );
      updateJson(newElements);
      return newElements;
    });
  };

  const loadRound1Template = () => {
    const templateElements: ReceiptElement[] = [
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'header', content: 'BYTE BURGERS', alignment: 'CENTER' },
      { id: generateId(), type: 'text', content: 'Store #001', alignment: 'CENTER' },
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'orderInfo', content: 'Order #A-0042   Date: 12/04/2024' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'itemHeader', content: 'ITEMS:' },
      { id: generateId(), type: 'item', content: 'Cheeseburger            x2 $17.98' },
      { id: generateId(), type: 'text', content: '@ $8.99 each' },
      { id: generateId(), type: 'item', content: 'French Fries            x1  $3.99' },
      { id: generateId(), type: 'item', content: 'Soft Drink              x2  $5.98' },
      { id: generateId(), type: 'text', content: '@ $2.99 each' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'subtotal', content: 'Subtotal:               $27.95' },
      { id: generateId(), type: 'tax', content: 'Tax (8.0%):             $2.24' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'total', content: 'TOTAL:                  $30.19' },
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'spacer', lines: 1 },
      { id: generateId(), type: 'thanks', content: '        Thank you for your order!', alignment: 'CENTER' },
      { id: generateId(), type: 'thanks', content: '           Have a great day!', alignment: 'CENTER' },
      { id: generateId(), type: 'spacer', lines: 1 },
      { id: generateId(), type: 'separator', content: '================================' },
    ];
    
    setElements(templateElements);
    updateJson(templateElements);
  };

  const loadRound2Template = () => {
    const templateElements: ReceiptElement[] = [
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'header', content: 'JAVA JUNCTION', alignment: 'CENTER' },
      { id: generateId(), type: 'text', content: 'Store #002', alignment: 'CENTER' },
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'orderInfo', content: 'Order #B-1337   Date: 12/04/2024' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'itemHeader', content: 'ITEMS:' },
      { id: generateId(), type: 'item', content: 'Large Latte             x2 $11.98' },
      { id: generateId(), type: 'text', content: '  @ $5.99 each' },
      { id: generateId(), type: 'itemPromotion', content: '  ** Buy One Get One 50% Off -$3.00 **' },
      { id: generateId(), type: 'item', content: 'Chocolate Croissant     x2  $9.00' },
      { id: generateId(), type: 'text', content: '  @ $4.50 each' },
      { id: generateId(), type: 'item', content: 'Breakfast Sandwich      x1  $7.99' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'subtotal', content: 'Subtotal:               $28.97' },
      { id: generateId(), type: 'discounts', content: 'Discounts:               -$3.00' },
      { id: generateId(), type: 'promotionHeader', content: 'PROMOTIONS:' },
      { id: generateId(), type: 'promotion', content: '  Morning Rush Special     -$2.00' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'subtotalAfter', content: 'Subtotal after discounts: $23.97' },
      { id: generateId(), type: 'tax', content: 'Tax (8.75%):             $2.53' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'total', content: 'TOTAL:                  $26.50' },
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'spacer', lines: 1 },
      { id: generateId(), type: 'thanks', content: '☕ Thanks for choosing Java Junction! ☕', alignment: 'CENTER' },
      { id: generateId(), type: 'loyalty', content: 'Loyalty points earned: 27', alignment: 'CENTER' },
      { id: generateId(), type: 'spacer', lines: 1 },
      { id: generateId(), type: 'separator', content: '================================' },
    ];
    
    setElements(templateElements);
    updateJson(templateElements);
  };

  const loadRound3Template = () => {
    const templateElements: ReceiptElement[] = [
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'header', content: 'PIZZA PALACE', alignment: 'CENTER' },
      { id: generateId(), type: 'text', content: 'Store #003', alignment: 'CENTER' },
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'orderInfo', content: 'Order #C-2024   Date: 12/04/2024' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'customerHeader', content: 'CUSTOMER INFORMATION:' },
      { id: generateId(), type: 'customerName', content: '  Name: John Doe' },
      { id: generateId(), type: 'memberStatus', content: '  Member Status: GOLD' },
      { id: generateId(), type: 'customerId', content: '  Customer ID: CUST-8826' },
      { id: generateId(), type: 'loyaltyPoints', content: '  Loyalty Points: 1,247' },
      { id: generateId(), type: 'memberSince', content: '  Member Since: 2019-03-15' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'itemHeader', content: 'ITEMS:' },
      { id: generateId(), type: 'item', content: 'Large Pepperoni Pizza   x1 $18.99' },
      { id: generateId(), type: 'item', content: 'Garlic Breadsticks      x2 $13.98' },
      { id: generateId(), type: 'text', content: '  @ $6.99 each' },
      { id: generateId(), type: 'item', content: '2-Liter Soda            x1  $3.99' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'subtotal', content: 'Subtotal:               $36.96' },
      { id: generateId(), type: 'tax', content: 'Tax (8.0%):             $2.96' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'total', content: 'TOTAL:                  $39.92' },
      { id: generateId(), type: 'paymentMethod', content: 'Payment Method: VISA ****1234' },
      { id: generateId(), type: 'separator', content: '--------------------------------' },
      { id: generateId(), type: 'loyaltyRewardsHeader', content: 'LOYALTY REWARDS:' },
      { id: generateId(), type: 'pointsEarned', content: '  Points Earned Today: 40' },
      { id: generateId(), type: 'totalPoints', content: '  Total Points: 1,287' },
      { id: generateId(), type: 'membershipStatus', content: '  Status: GOLD Member' },
      { id: generateId(), type: 'separator', content: '================================' },
      { id: generateId(), type: 'spacer', lines: 1 },
      { id: generateId(), type: 'thanks', content: 'Thank you for being a loyal customer!', alignment: 'CENTER' },
      { id: generateId(), type: 'thanks', content: 'See you again soon!', alignment: 'CENTER' },
      { id: generateId(), type: 'spacer', lines: 1 },
      { id: generateId(), type: 'separator', content: '================================' },
    ];
    
    setElements(templateElements);
    updateJson(templateElements);
  };

  const handleDragStart = (e: React.DragEvent, item: DragItem) => {
    setDraggedItem(item);
    e.dataTransfer.effectAllowed = 'copy';
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'copy';
  };

  const handleDrop = (e: React.DragEvent, index?: number) => {
    e.preventDefault();
    if (draggedItem) {
      addElement(draggedItem, index);
      setDraggedItem(null);
    }
  };

  return (
    <div className="h-full bg-gray-900 flex flex-col overflow-hidden">
      <div className="flex-1 flex gap-4 p-4 overflow-hidden">
        {/* Element Palette */}
        <div className="w-64 bg-gray-800 rounded-lg p-4 flex flex-col overflow-hidden">
          <h3 className="text-lg font-bold text-white mb-4">🧰 Elements</h3>
          
          <button
            onClick={loadRound1Template}
            className="w-full mb-2 px-3 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm flex-shrink-0"
          >
            📋 Load Round 1 Template
          </button>
          
          <button
            onClick={loadRound2Template}
            className="w-full mb-2 px-3 py-2 bg-purple-600 text-white rounded hover:bg-purple-700 text-sm flex-shrink-0"
          >
            ☕ Load Round 2 Template
          </button>
          
          <button
            onClick={loadRound3Template}
            className="w-full mb-4 px-3 py-2 bg-red-600 text-white rounded hover:bg-red-700 text-sm flex-shrink-0"
          >
            🍕 Load Round 3 Template
          </button>

          <div className="space-y-2 overflow-y-auto flex-1">
            {ELEMENT_TYPES.map(item => (
              <div
                key={item.type}
                draggable
                onDragStart={(e) => handleDragStart(e, item)}
                className="p-3 bg-gray-700 rounded cursor-move hover:bg-gray-600 text-white text-sm border border-gray-600 flex-shrink-0"
              >
                {item.label}
              </div>
            ))}
          </div>
        </div>

        {/* Receipt Canvas */}
        <div className="flex-1 bg-gray-800 rounded-lg p-4 flex flex-col overflow-hidden">
          <h3 className="text-lg font-bold text-white mb-4 flex-shrink-0">🧾 Receipt Design</h3>
          
          <div 
            className="bg-white p-6 rounded-lg font-mono text-sm flex-1 overflow-y-auto"
            onDragOver={handleDragOver}
            onDrop={(e) => handleDrop(e)}
          >
            {elements.length === 0 ? (
              <div className="text-gray-500 text-center py-8 border-2 border-dashed border-gray-300 rounded">
                Drag elements here to design your receipt
              </div>
            ) : (
              elements.map((element, index) => (
                <div
                  key={element.id}
                  className={`receipt-line group relative ${element.alignment === 'CENTER' ? 'text-center' : element.alignment === 'RIGHT' ? 'text-right' : 'text-left'} 
                    ${selectedElement === element.id ? 'bg-blue-100' : 'hover:bg-gray-100'} py-1`}
                  onClick={() => setSelectedElement(element.id)}
                  onDragOver={handleDragOver}
                  onDrop={(e) => handleDrop(e, index)}
                >
                  {element.type === 'spacer' ? (
                    <div className="h-4 border-dashed border border-gray-300 rounded bg-gray-50 flex items-center justify-center text-xs text-gray-400">
                      {element.lines || 1} line{(element.lines || 1) > 1 ? 's' : ''}
                    </div>
                  ) : (
                    <pre className="break-all whitespace-pre-wrap font-mono m-0">{element.content || '[Empty]'}</pre>
                  )}
                  
                  <button
                    onClick={(e) => { e.stopPropagation(); removeElement(element.id); }}
                    className="absolute right-0 top-0 opacity-0 group-hover:opacity-100 bg-red-500 text-white text-xs px-1 rounded"
                  >
                    ✕
                  </button>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Properties Panel */}
        {selectedElement && (
          <div className="w-64 bg-gray-800 rounded-lg p-4 flex flex-col overflow-hidden">
            <h3 className="text-lg font-bold text-white mb-4 flex-shrink-0">⚙️ Properties</h3>
            
            <div className="flex-1 overflow-y-auto">
              {(() => {
                const element = elements.find(el => el.id === selectedElement);
                if (!element) return null;

                return (
                  <div className="space-y-4">
                    {element.type !== 'spacer' && (
                      <div>
                        <label className="block text-white text-sm mb-2">Content:</label>
                        <textarea
                          value={element.content || ''}
                          onChange={(e) => updateElement(element.id, { content: e.target.value })}
                          className="w-full p-2 bg-gray-700 text-white rounded text-sm resize-none"
                          rows={4}
                        />
                      </div>
                    )}

                    <div>
                      <label className="block text-white text-sm mb-2">Alignment:</label>
                      <select
                        value={element.alignment || 'LEFT'}
                        onChange={(e) => updateElement(element.id, { alignment: e.target.value })}
                        className="w-full p-2 bg-gray-700 text-white rounded text-sm"
                      >
                        <option value="LEFT">Left</option>
                        <option value="CENTER">Center</option>
                        <option value="RIGHT">Right</option>
                      </select>
                    </div>

                    {element.type === 'spacer' && (
                      <div>
                        <label className="block text-white text-sm mb-2">Lines:</label>
                        <input
                          type="number"
                          min="1"
                          max="5"
                          value={element.lines || 1}
                          onChange={(e) => updateElement(element.id, { lines: parseInt(e.target.value) })}
                          className="w-full p-2 bg-gray-700 text-white rounded text-sm"
                        />
                      </div>
                    )}
                  </div>
                );
              })()}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};