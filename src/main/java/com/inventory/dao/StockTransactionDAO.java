package com.inventory.dao;

import com.inventory.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockTransactionDAO {

    // ACID Transaction Method
    public boolean processStock(int productId, String type, int qty) {
        String checkStockSql = "SELECT quantity FROM products WHERE product_id = ?";
        String updateStockSql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
        String insertTxSql = "INSERT INTO stock_transactions (product_id, transaction_type, quantity) VALUES (?, ?, ?)";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            // 1. Auto-commit off: Ab hum decide karenge kab save karna hai
            conn.setAutoCommit(false);

            // 2. OUT transaction me check karein ki sufficient stock hai ya nahi
            if ("OUT".equalsIgnoreCase(type)) {
                try (PreparedStatement checkStmt = conn.prepareStatement(checkStockSql)) {
                    checkStmt.setInt(1, productId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        int currentQty = rs.getInt("quantity");
                        if (currentQty < qty) {
                            System.err.println("Transaction Failed: Insufficient stock (Available: " + currentQty + ")");
                            conn.rollback(); // Rollback
                            return false;
                        }
                    } else {
                        System.err.println("Transaction Failed: Product ID not found.");
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 3. Stock update karein (IN me +qty, OUT me -qty)
            int stockDelta = "IN".equalsIgnoreCase(type) ? qty : -qty;
            try (PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
                updateStmt.setInt(1, stockDelta);
                updateStmt.setInt(2, productId);
                updateStmt.executeUpdate();
            }

            // 4. Audit Log insert karein
            try (PreparedStatement txStmt = conn.prepareStatement(insertTxSql)) {
                txStmt.setInt(1, productId);
                txStmt.setString(2, type.toUpperCase());
                txStmt.setInt(3, qty);
                txStmt.executeUpdate();
            }

            // 5. Agar yahan tak sab pass ho gaya, to permanent SAVE karein
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error processing transaction: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Error aane par rollback
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}