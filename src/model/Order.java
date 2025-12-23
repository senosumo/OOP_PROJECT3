package model;

public class Order {
    private int id;
    private String orderTime;
    private String deliveryTime;
    private double totalCost;
    private boolean delivered;

    public Order(int id, String orderTime, String deliveryTime, double totalCost, boolean delivered) {
        this.id = id;
        this.orderTime = orderTime;
        this.deliveryTime = deliveryTime;
        this.totalCost = totalCost;
        this.delivered = delivered;
    }
}
