package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAdapter {

    private Connection conn;

    public DatabaseAdapter() {
        try {
            // Connect to your MySQL database
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/greengrocer_db",
                    "myuser",       // your MySQL username
                    "1234"          // your MySQL password
            );
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Cannot connect to the database.");
        }
    }

    /**
     * Check if the username and password exist in the database
     */
    public String validateLogin(String username, String password) {
        String role = null;
        String query = "SELECT role FROM UserInfo WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = rs.getString("role");  // could be "customer", "carrier", or "owner"
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    // Optional: close connection
    public void close() {
        try {
            if (conn != null && !conn.isClosed())
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
