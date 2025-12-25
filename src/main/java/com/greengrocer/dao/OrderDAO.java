package com.greengrocer.dao;

import com.greengrocer.model.Order;
import com.greengrocer.model.OrderItem;
import com.greengrocer.util.DatabaseAdapter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection connection;

    public OrderDAO() {
        this.connection = DatabaseAdapter.getInstance().getConnection();
    }

    public boolean placeOrder(Order order) {
        String insertOrder = "INSERT INTO OrderInfo (customer_id, order_time, delivery_time, status, total_cost, invoice_data) VALUES (?, ?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO OrderItems (order_id, product_id, quantity, price_per_kg) VALUES (?, ?, ?, ?)";
        
        try {
            connection.setAutoCommit(false); // Start Transaction

            int orderId = -1;
            try (PreparedStatement pstmt = connection.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getCustomerId());
                pstmt.setTimestamp(2, Timestamp.valueOf(order.getOrderTime()));
                pstmt.setTimestamp(3, Timestamp.valueOf(order.getDeliveryTime()));
                pstmt.setString(4, order.getStatus());
                pstmt.setDouble(5, order.getTotalCost());
                pstmt.setString(6, order.getInvoiceData());
                
                int rows = pstmt.executeUpdate();
                if (rows == 0) throw new SQLException("Creating order failed, no rows affected.");
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                        order.setId(orderId);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            try (PreparedStatement pstmt = connection.prepareStatement(insertItem)) {
                for (OrderItem item : order.getItems()) {
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, item.getProductId());
                    pstmt.setDouble(3, item.getQuantity());
                    pstmt.setDouble(4, item.getPricePerKg());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            
            // Deduct stock
            ProductDAO productDAO = new ProductDAO();
            for (OrderItem item : order.getItems()) {
                // Warning: This simplistic stock update doesn't check for race conditions
                // Ideally should be "UPDATE ProductInfo SET stock = stock - ? WHERE id = ? AND stock >= ?"
                String updateStock = "UPDATE ProductInfo SET stock = stock - ? WHERE id = ?";
                 try (PreparedStatement pStock = connection.prepareStatement(updateStock)) {
                     pStock.setDouble(1, item.getQuantity());
                     pStock.setInt(2, item.getProductId());
                     pStock.executeUpdate();
                 }
            }

            connection.commit(); // Commit Transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        String query = "SELECT * FROM OrderInfo WHERE customer_id = ? ORDER BY order_time DESC";
        return getOrdersWithQuery(query, customerId);
    }
    
    public List<Order> getAllOrders() {
        String query = "SELECT * FROM OrderInfo ORDER BY order_time DESC";
        return getOrdersWithQuery(query, null);
    }
    
    public List<Order> getAvailableOrders() {
         String query = "SELECT * FROM OrderInfo WHERE status = 'Pending' ORDER BY delivery_time ASC";
         return getOrdersWithQuery(query, null);
    }
    
    public List<Order> getOrdersByCarrier(int carrierId) {
        String query = "SELECT * FROM OrderInfo WHERE carrier_id = ? ORDER BY delivery_time ASC";
        return getOrdersWithQuery(query, carrierId);
    }

    private List<Order> getOrdersWithQuery(String query, Integer param) {
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (param != null) {
                pstmt.setInt(1, param);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setCarrierId(rs.getInt("carrier_id"));
                    order.setOrderTime(rs.getTimestamp("order_time") != null ? rs.getTimestamp("order_time").toLocalDateTime() : null);
                    order.setDeliveryTime(rs.getTimestamp("delivery_time") != null ? rs.getTimestamp("delivery_time").toLocalDateTime() : null);
                    order.setStatus(rs.getString("status"));
                    order.setTotalCost(rs.getDouble("total_cost"));
                    order.setInvoiceData(rs.getString("invoice_data"));
                    
                     // Fetch Items
                    order.setItems(getOrderItems(order.getId()));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    private List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT oi.*, p.name FROM OrderItems oi JOIN ProductInfo p ON oi.product_id = p.id WHERE order_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getDouble("quantity"),
                        rs.getDouble("price_per_kg")
                    );
                    item.setProductName(rs.getString("name"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public boolean updateOrderStatus(int orderId, String status) {
        String query = "UPDATE OrderInfo SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
             e.printStackTrace();
             return false;
        }
    }
    
    public boolean assignCarrier(int orderId, int carrierId) {
         String query = "UPDATE OrderInfo SET carrier_id = ?, status = 'Selected' WHERE id = ? AND status = 'Pending'";
          try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, carrierId);
            pstmt.setInt(2, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
             e.printStackTrace();
             return false;
        }
    }
}
