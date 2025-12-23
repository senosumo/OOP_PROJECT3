CREATE DATABASE IF NOT EXISTS greengrocer_db;
USE greengrocer_db;

-- Users Table
CREATE TABLE IF NOT EXISTS UserInfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('customer', 'carrier', 'owner') NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    loyalty_points INT DEFAULT 0
);

-- Products Table
CREATE TABLE IF NOT EXISTS ProductInfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type ENUM('fruit', 'vegetable') NOT NULL,
    price DOUBLE NOT NULL,
    stock DOUBLE NOT NULL,
    threshold DOUBLE NOT NULL DEFAULT 5.0,
    image LONGBLOB
);

-- Orders Table
CREATE TABLE IF NOT EXISTS OrderInfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    carrier_id INT,
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    delivery_time DATETIME,
    status ENUM('Pending', 'Selected', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    total_cost DOUBLE NOT NULL,
    invoice_data LONGTEXT, -- CLOB for invoice log
    FOREIGN KEY (customer_id) REFERENCES UserInfo(id),
    FOREIGN KEY (carrier_id) REFERENCES UserInfo(id)
);

-- Order Items (Linking Products to Orders)
CREATE TABLE IF NOT EXISTS OrderItems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity DOUBLE NOT NULL,
    price_per_kg DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES OrderInfo(id),
    FOREIGN KEY (product_id) REFERENCES ProductInfo(id)
);

-- Messages (Optional for communication)
CREATE TABLE IF NOT EXISTS Messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT,
    receiver_id INT, -- NULL for 'broadcast' or Owner
    content TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES UserInfo(id)
);

-- Initial Users
INSERT INTO UserInfo (username, password, role, address) VALUES 
('cust', 'cust', 'customer', '123 Customer Lane'),
('carr', 'carr', 'carrier', '456 Carrier Way'),
('own', 'own', 'owner', '789 Owner Street')
ON DUPLICATE KEY UPDATE password=password;

-- Sample Products (Images should be updated via app or separate script)
INSERT INTO ProductInfo (name, type, price, stock, threshold) VALUES 
('Apple', 'fruit', 2.5, 100, 10),
('Banana', 'fruit', 1.8, 80, 10),
('Orange', 'fruit', 3.0, 90, 10),
('Strawberry', 'fruit', 5.0, 50, 5),
('Grapes', 'fruit', 4.5, 60, 5),
('Watermelon', 'fruit', 1.2, 40, 5),
('Potato', 'vegetable', 0.8, 200, 20),
('Tomato', 'vegetable', 1.5, 150, 15),
('Onion', 'vegetable', 0.9, 180, 20),
('Carrot', 'vegetable', 1.2, 120, 15),
('Broccoli', 'vegetable', 2.8, 70, 10),
('Spinach', 'vegetable', 1.5, 60, 10);
