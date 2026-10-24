package com.inventory.service;

import com.inventory.dao.ProductDAO;
import com.inventory.model.Product;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class InventoryAlertService {

    private final ProductDAO productDAO;

    public InventoryAlertService() {
        this.productDAO = new ProductDAO();
    }

    // Min-Heap logic: Sabse kam stock wala product top par rahega
    public PriorityQueue<Product> getLowStockAlerts() {
        List<Product> allProducts = productDAO.getAllProducts();

        // Min-Heap configured by current quantity ascending
        PriorityQueue<Product> minHeap = new PriorityQueue<>(Comparator.comparingInt(Product::getQuantity));

        for (Product p : allProducts) {
            // Agar quantity minimum reorder level ke barabar ya kam ho chuki hai
            if (p.getQuantity() <= p.getMinReorderLevel()) {
                minHeap.add(p);
            }
        }

        return minHeap;
    }
}