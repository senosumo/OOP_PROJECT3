package com.greengrocer.model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String productName; // Transient, for display
    private double quantity;
    private double pricePerKg;

    public OrderItem(int id, int orderId, int productId, double quantity, double pricePerKg) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerKg = pricePerKg;
    }
    
    public OrderItem(Product product, double quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.quantity = quantity;
        this.pricePerKg = product.getCurrentPrice();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(double pricePerKg) { this.pricePerKg = pricePerKg; }
    
    public double getTotalPrice() {
        return quantity * pricePerKg;
    }
}
