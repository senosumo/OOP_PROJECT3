package com.greengrocer.dao;

import com.greengrocer.model.Product;
import com.greengrocer.util.DatabaseAdapter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        this.connection = DatabaseAdapter.getInstance().getConnection();
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM ProductInfo ORDER BY name";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM ProductInfo WHERE name LIKE ? ORDER BY name";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Blob blob = rs.getBlob("image");
        Image image = null;
        if (blob != null) {
            image = new Image(blob.getBinaryStream());
        }
        
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("type"),
            rs.getDouble("price"),
            rs.getDouble("stock"),
            rs.getDouble("threshold"),
            image
        );
    }

    public boolean updateStock(int productId, double newStock) {
        String query = "UPDATE ProductInfo SET stock = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, newStock);
            pstmt.setInt(2, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addProduct(Product product) {
        String query = "INSERT INTO ProductInfo (name, type, price, stock, threshold, image) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getType());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setDouble(4, product.getStock());
            pstmt.setDouble(5, product.getThreshold());
            
            if (product.getImageStream() != null) {
                 pstmt.setBlob(6, product.getImageStream());
            } else {
                 pstmt.setNull(6, Types.BLOB);
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method for owner to update product details including image
    public boolean updateProduct(Product product) {
         StringBuilder queryBuilder = new StringBuilder("UPDATE ProductInfo SET name=?, type=?, price=?, stock=?, threshold=?");
         boolean hasImage = product.getImageStream() != null;
         if (hasImage) {
             queryBuilder.append(", image=?");
         }
         queryBuilder.append(" WHERE id=?");
         
         try (PreparedStatement pstmt = connection.prepareStatement(queryBuilder.toString())) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getType());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setDouble(4, product.getStock());
            pstmt.setDouble(5, product.getThreshold());
            
            int paramIndex = 6;
            if (hasImage) {
                 pstmt.setBlob(paramIndex++, product.getImageStream());
            }
            pstmt.setInt(paramIndex, product.getId());
            
            return pstmt.executeUpdate() > 0;
         } catch (SQLException e) {
            e.printStackTrace();
            return false;
         }
    }
    
    public boolean deleteProduct(int id) {
        String query = "DELETE FROM ProductInfo WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
