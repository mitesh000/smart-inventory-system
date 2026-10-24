package com.inventory;

import com.inventory.dao.CategoryDAO;
import com.inventory.dao.ProductDAO;
import com.inventory.dao.StockTransactionDAO;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.service.InventoryAlertService;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CategoryDAO categoryDAO = new CategoryDAO();
        ProductDAO productDAO = new ProductDAO();
        StockTransactionDAO txDAO = new StockTransactionDAO();
        InventoryAlertService alertService = new InventoryAlertService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== INVENTORY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Category");
            System.out.println("2. View All Categories");
            System.out.println("3. Add Product");
            System.out.println("4. View All Products");
            System.out.println("5. Stock IN (Add Stock)");
            System.out.println("6. Stock OUT (Sell/Dispatch Stock)");
            System.out.println("7. View Low-Stock Alerts (Min-Heap Priority)");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Category Name: ");
                    String catName = sc.nextLine();
                    if (categoryDAO.addCategory(new Category(catName))) {
                        System.out.println("Category added successfully!");
                    } else {
                        System.out.println("Failed to add category.");
                    }
                    break;

                case 2:
                    List<Category> categories = categoryDAO.getAllCategories();
                    System.out.println("\n--- Categories ---");
                    if (categories.isEmpty()) {
                        System.out.println("No categories found.");
                    } else {
                        for (Category c : categories) {
                            System.out.println(c);
                        }
                    }
                    break;

                case 3:
                    System.out.print("Enter Product SKU (e.g., EL-101): ");
                    String sku = sc.nextLine();

                    System.out.print("Enter Product Name: ");
                    String prodName = sc.nextLine();

                    System.out.print("Enter Category ID: ");
                    int catId = sc.nextInt();

                    System.out.print("Enter Price: ");
                    double price = sc.nextDouble();

                    System.out.print("Enter Initial Stock Quantity: ");
                    int qty = sc.nextInt();

                    System.out.print("Enter Min Reorder Level: ");
                    int reorder = sc.nextInt();

                    Product product = new Product(sku, prodName, catId, price, qty, reorder);
                    if (productDAO.addProduct(product)) {
                        System.out.println("Product added successfully!");
                    } else {
                        System.out.println("Failed to add product.");
                    }
                    break;

                case 4:
                    List<Product> products = productDAO.getAllProducts();
                    System.out.println("\n--- Inventory Products ---");
                    if (products.isEmpty()) {
                        System.out.println("No products found in inventory.");
                    } else {
                        for (Product p : products) {
                            System.out.println(p);
                        }
                    }
                    break;

                case 5:
                    System.out.print("Enter Product ID to ADD stock: ");
                    int inProdId = sc.nextInt();
                    System.out.print("Enter quantity to add: ");
                    int inQty = sc.nextInt();

                    if (txDAO.processStock(inProdId, "IN", inQty)) {
                        System.out.println("Stock added and transaction logged successfully!");
                    } else {
                        System.out.println("Stock IN transaction failed.");
                    }
                    break;

                case 6:
                    System.out.print("Enter Product ID to SELL/DISPATCH: ");
                    int outProdId = sc.nextInt();
                    System.out.print("Enter quantity to dispatch: ");
                    int outQty = sc.nextInt();

                    if (txDAO.processStock(outProdId, "OUT", outQty)) {
                        System.out.println("Stock dispatched and transaction logged successfully!");
                    } else {
                        System.out.println("Stock OUT transaction failed.");
                    }
                    break;

                case 7:
                    PriorityQueue<Product> alertQueue = alertService.getLowStockAlerts();
                    System.out.println("\n--- CRITICAL REORDER ALERTS (Min-Heap Priority) ---");
                    if (alertQueue.isEmpty()) {
                        System.out.println("All product stocks are healthy!");
                    } else {
                        while (!alertQueue.isEmpty()) {
                            Product lowStockItem = alertQueue.poll();
                            System.out.printf("[CRITICAL ALERT] SKU: %s | Name: %s | Current Stock: %d (Reorder Level: %d)%n",
                                    lowStockItem.getSkuCode(),
                                    lowStockItem.getName(),
                                    lowStockItem.getQuantity(),
                                    lowStockItem.getMinReorderLevel());
                       }
                    }
                    break;

                case 8:
                    System.out.println("Exiting system. Goodbye!");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}