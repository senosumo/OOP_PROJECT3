package model;

public class Product {
    private int id;
    private String name;
    private String type;
    private double price;
    private double stock;
    private double threshold;

    public Product(int id, String name, String type, double price, double stock, double threshold) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.stock = stock;
        this.threshold = threshold;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
