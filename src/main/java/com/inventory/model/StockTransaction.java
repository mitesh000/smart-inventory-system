package com.inventory.model;

import java.sql.Timestamp;

public class StockTransaction {
    private int transactionId;
    private int productId;
    private String transactionType; // "IN" ya "OUT"
    private int quantity;
    private Timestamp transactionDate;

    public StockTransaction(int productId, String transactionType, int quantity) {
        this.productId = productId;
        this.transactionType = transactionType;
        this.quantity = quantity;
    }

    public int getProductId() { return productId; }
    public String getTransactionType() { return transactionType; }
    public int getQuantity() { return quantity; }
}