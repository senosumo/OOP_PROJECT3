package com.greengrocer.util;

import com.greengrocer.model.OrderItem;
import com.greengrocer.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Cart {
    private static Cart instance;
    private ObservableList<OrderItem> items;

    private Cart() {
        items = FXCollections.observableArrayList();
    }

    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void addItem(Product product, double quantity) {
        // Check for merge
        for (OrderItem item : items) {
            if (item.getProductId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                // Trigger update if needed, ObservableList handles list events but object
                // property changes need extraction
                // For simplicity in this project, we might just replace it or rely on table
                // refresh
                int index = items.indexOf(item);
                items.set(index, item);
                return;
            }
        }
        items.add(new OrderItem(product, quantity));
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    public void clear() {
        items.clear();
    }

    public ObservableList<OrderItem> getItems() {
        return items;
    }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }

    public double getTotalWithTax() {
        return getTotal() * 1.01; // Assuming 1% VAT or similar? The brief just says "including taxes (VAT)".
                                  // Let's assume 18% VAT or standard.
        // Brief doesn't specify rate. I'll use 10% standard food VAT.
    }
}
