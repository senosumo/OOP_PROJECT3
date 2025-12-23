package com.greengrocer.model;

import javafx.scene.image.Image;
import java.io.InputStream;

public class Product {
    private int id;
    private String name;
    private String type;
    private double price;
    private double stock;
    private double threshold;
    private Image image; // JavaFX Image
    private InputStream imageStream; // For saving back to DB

    public Product(int id, String name, String type, double price, double stock, double threshold, Image image) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.stock = stock;
        this.threshold = threshold;
        this.image = image;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }
    
    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    public InputStream getImageStream() { return imageStream; }
    public void setImageStream(InputStream imageStream) { this.imageStream = imageStream; }
    
    // Helper to check if prices should be doubled
    public double getCurrentPrice() {
        if (stock <= threshold) {
            return price * 2;
        }
        return price;
    }
}
