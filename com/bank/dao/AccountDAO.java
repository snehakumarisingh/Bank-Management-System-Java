package com.bank.dao;

import com.bank.model.Account;
import com.bank.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Account Data Access Object
 * Handles all CRUD operations for accounts using JDBC
 */
public class AccountDAO {

    private Connection conn;

    public AccountDAO() {
        this.conn = DBConnection.getConnection();
    }

    // Generate unique account number
    private String generateAccountNumber() {
        Random rand = new Random();
        return "ACC" + (1000000000L + (long)(rand.nextDouble() * 9000000000L));
    }

    // Create a new account
    public boolean createAccount(Account account) {
        String accountNumber = generateAccountNumber();
        account.setAccountNumber(accountNumber);

        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNumber());
            ps.setInt(2, account.getCustomerId());
            ps.setString(3, account.getAccountType());
            ps.setDouble(4, account.getBalance());
            ps.setString(5, "active");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
            return false;
        }
    }

    // Get account by account number
    public Account getAccount(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account acc = new Account();
                acc.setAccountNumber(rs.getString("account_number"));
                acc.setCustomerId(rs.getInt("customer_id"));
                acc.setAccountType(rs.getString("account_type"));
                acc.setBalance(rs.getDouble("balance"));
                acc.setStatus(rs.getString("status"));
                return acc;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account: " + e.getMessage());
        }
        return null;
    }

    // Get all accounts by customer ID
    public List<Account> getAccountsByCustomer(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Account acc = new Account();
                acc.setAccountNumber(rs.getString("account_number"));
                acc.setCustomerId(rs.getInt("customer_id"));
                acc.setAccountType(rs.getString("account_type"));
                acc.setBalance(rs.getDouble("balance"));
                acc.setStatus(rs.getString("status"));
                accounts.add(acc);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching accounts: " + e.getMessage());
        }
        return accounts;
    }

    // Get all accounts (admin view)
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, c.full_name FROM accounts a JOIN customers c ON a.customer_id = c.id";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Account acc = new Account();
                acc.setAccountNumber(rs.getString("account_number"));
                acc.setCustomerId(rs.getInt("customer_id"));
                acc.setAccountType(rs.getString("account_type"));
                acc.setBalance(rs.getDouble("balance"));
                acc.setStatus(rs.getString("status"));
                accounts.add(acc);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all accounts: " + e.getMessage());
        }
        return accounts;
    }

    // Update balance
    public boolean updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
            return false;
        }
    }

    // Close account
    public boolean closeAccount(String accountNumber) {
        String sql = "UPDATE accounts SET status = 'closed' WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error closing account: " + e.getMessage());
            return false;
        }
    }
}
