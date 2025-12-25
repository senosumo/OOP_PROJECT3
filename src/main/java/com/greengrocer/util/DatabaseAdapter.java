package com.greengrocer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseAdapter {
    private static DatabaseAdapter instance;
    private Connection connection;
    
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/greengrocer_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "myuser";
    private static final String PASSWORD = "1234";

    private DatabaseAdapter() {
        try {
            // Load driver if needed (modern JDBC drivers often auto-load, but good for safety)
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.err.println("Database Connection Failed!");
        }
    }

    public static synchronized DatabaseAdapter getInstance() {
        if (instance == null) {
            instance = new DatabaseAdapter();
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance = new DatabaseAdapter();
                }
            } catch (SQLException e) {
                instance = new DatabaseAdapter();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
