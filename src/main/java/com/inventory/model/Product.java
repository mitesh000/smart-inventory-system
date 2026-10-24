package com.inventory.model;

public class Product {
    private int productId;
    private String skuCode;
    private String name;
    private int categoryId;
    private double price;
    private int quantity;
    private int minReorderLevel;

    // 1. Data insert karne ke liye constructor (ID database dega)
    public Product(String skuCode, String name, int categoryId, double price, int quantity, int minReorderLevel) {
        this.skuCode = skuCode;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
        this.minReorderLevel = minReorderLevel;
    }

    // 2. Database se read karne ke liye constructor (ID ke sath)
    public Product(int productId, String skuCode, String name, int categoryId, double price, int quantity, int minReorderLevel) {
        this.productId = productId;
        this.skuCode = skuCode;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
        this.minReorderLevel = minReorderLevel;
    }

    // Getters and Setters (Encapsulation)
    public int getProductId() { return productId; }
    public String getSkuCode() { return skuCode; }
    public String getName() { return name; }
    public int getCategoryId() { return categoryId; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getMinReorderLevel() { return minReorderLevel; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("ID: %-3d | SKU: %-8s | Name: %-15s | Qty: %-4d | Price: ₹%-8.2f | MinAlert: %-3d",
                productId, skuCode, name, quantity, price, minReorderLevel);
    }
}