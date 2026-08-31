package com.bank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility
 * Handles MySQL JDBC connection using Singleton pattern
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/bank_management";
    private static final String USER = "root";       // Change to your MySQL username
    private static final String PASSWORD = "manager";   // Change to your MySQL password

    private static Connection connection = null;

    // Private constructor - Singleton pattern
    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✔ Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("✘ MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("✘ Database connection failed: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
