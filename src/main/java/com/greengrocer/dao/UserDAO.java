package com.greengrocer.dao;

import com.greengrocer.model.User;
import com.greengrocer.util.DatabaseAdapter;
import java.sql.*;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseAdapter.getInstance().getConnection();
    }

    public User login(String username, String password) {
        String query = "SELECT * FROM UserInfo WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getInt("loyalty_points")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User user, String password) {
        String query = "INSERT INTO UserInfo (username, password, role, address, phone) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, password);
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, user.getPhone());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        String query = "UPDATE UserInfo SET address = ?, phone = ?, loyalty_points = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getAddress());
            pstmt.setString(2, user.getPhone());
            pstmt.setInt(3, user.getLoyaltyPoints());
            pstmt.setInt(4, user.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM UserInfo WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
