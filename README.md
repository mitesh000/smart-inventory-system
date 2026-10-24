# 📦 Smart Warehouse & Inventory Management System

A production-grade Java enterprise backend application designed for real-time inventory tracking, warehouse operations, and dynamic stock reordering. Built with standard Layered Architecture (Model-DAO-Service pattern), enterprise JDBC with ACID-compliant transactions, and Data Structures & Algorithms (Min-Heap / PriorityQueue).

---

## 🛠 Tech Stack
- **Language:** Java 17
- **Database:** MySQL 8.x
- **Build Tool:** Apache Maven
- **Driver:** MySQL Connector/J (`8.3.0`)
- **Data Structures & Algorithms:** PriorityQueue (Min-Heap), Hash-based Collections

---

## 🏛 System Architecture
The application strictly adheres to the Single Responsibility Principle (SRP) and separation of concerns via a layered package hierarchy:

- `com.inventory.model` — Domain entity POJOs (`Product`, `Category`, `StockTransaction`) with strict encapsulation.
- `com.inventory.dao` — Data Access Objects handling raw SQL statements, relational mappings, and JDBC transaction lifecycles.
- `com.inventory.service` — Core business rules and heuristic algorithms (low-stock threshold monitoring).
- `com.inventory.util` — Centralized database connection factory and lifecycle manager.
- `com.inventory.Main` — Interactive Command-Line Interface (CLI) application orchestrator.

---

## 🚀 Key Features & Engineering Highlights

### 1. Relational Database Design & Constraints
- Normalized MySQL relational schema comprising 3 interrelated tables: `categories`, `products`, and `stock_transactions`.
- Enforces relational data integrity via **Foreign Keys** (`ON DELETE RESTRICT` / `CASCADE`).
- Strict constraints: Unique SKU indexing and `CHECK (quantity >= 0)` to eliminate negative stock states at the database layer.

### 2. ACID-Compliant Transaction Management
- Implements manual transaction control (`conn.setAutoCommit(false)`, `commit()`, `rollback()`).
- Atomic stock movements: During a stock dispatch (`OUT`), the system atomically audits transaction history and updates product balances.
- Prevents dirty reads and state corruption by initiating automatic rollbacks if requested dispatch quantities exceed real-time warehouse inventory.

### 3. Dynamic Low-Stock Alert System (Min-Heap)
- Leverages Java's `PriorityQueue` configured as a **Min-Heap** using a custom comparator (`Comparator.comparingInt(Product::getQuantity)`).
- Dynamically filters inventory items where `current_stock <= min_reorder_level`.
- Delivers $O(1)$ lookup for the most critical stock-out risks to warehouse operators.

---

## ⚙ Database Schema Setup

```sql
CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    sku_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    min_reorder_level INT NOT NULL DEFAULT 5,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    CONSTRAINT chk_quantity CHECK (quantity >= 0)
);

CREATE TABLE stock_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    transaction_type ENUM('IN', 'OUT') NOT NULL,
    quantity INT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
