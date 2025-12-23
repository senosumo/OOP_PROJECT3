package com.greengrocer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int customerId;
    private int carrierId;
    private LocalDateTime orderTime;
    private LocalDateTime deliveryTime;
    private String status;
    private double totalCost;
    private String invoiceData;
    private List<OrderItem> items;

    public Order(int id, int customerId, int carrierId, LocalDateTime orderTime, LocalDateTime deliveryTime, String status, double totalCost) {
        this.id = id;
        this.customerId = customerId;
        this.carrierId = carrierId;
        this.orderTime = orderTime;
        this.deliveryTime = deliveryTime;
        this.status = status;
        this.totalCost = totalCost;
        this.items = new ArrayList<>();
    }

    public Order() {
        this.items = new ArrayList<>();
        this.status = "Pending";
        this.orderTime = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getCarrierId() { return carrierId; }
    public void setCarrierId(int carrierId) { this.carrierId = carrierId; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public LocalDateTime getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(LocalDateTime deliveryTime) { this.deliveryTime = deliveryTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    
    public String getInvoiceData() { return invoiceData; }
    public void setInvoiceData(String invoiceData) { this.invoiceData = invoiceData; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public void addItem(OrderItem item) {
        this.items.add(item);
    }
}
