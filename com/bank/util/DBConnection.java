package com.bank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bank_management";
    private static final String USER = "root";
    private static final String PASSWORD = "manager";

    private static Connection connection = null;

    private DBConnection() {
    }

    public static Connection getConnection() {

        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection(
                        URL, USER, PASSWORD);

                System.out.println("Database connected successfully.");
            }

        } catch (ClassNotFoundException e) {
            System.out.println(
                    "MySQL driver not found: " + e.getMessage());

        } catch (SQLException e) {
            System.out.println(
                    "Database connection failed: " + e.getMessage());
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
            System.out.println(
                    "Error closing connection: " + e.getMessage());
        }
    }
}