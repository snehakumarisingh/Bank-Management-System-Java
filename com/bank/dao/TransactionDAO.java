package com.bank.dao;

import com.bank.model.Transaction;
import com.bank.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Data Access Object
 * Handles recording and fetching transactions using JDBC
 */
public class TransactionDAO {

    private Connection conn;

    public TransactionDAO() {
        this.conn = DBConnection.getConnection();
    }

    // Record a new transaction
    public boolean recordTransaction(Transaction txn) {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, balance_after, description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txn.getAccountNumber());
            ps.setString(2, txn.getTransactionType());
            ps.setDouble(3, txn.getAmount());
            ps.setDouble(4, txn.getBalanceAfter());
            ps.setString(5, txn.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error recording transaction: " + e.getMessage());
            return false;
        }
    }

    // Get all transactions for an account
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapTransaction(rs));
        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return list;
    }

    // Get last N transactions for an account
    public List<Transaction> getLastNTransactions(String accountNumber, int n) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.setInt(2, n);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapTransaction(rs));
        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return list;
    }

    // Get all transactions (admin view)
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapTransaction(rs));
        } catch (SQLException e) {
            System.out.println("Error fetching all transactions: " + e.getMessage());
        }
        return list;
    }

    // Helper: map ResultSet to Transaction
    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setAccountNumber(rs.getString("account_number"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setAmount(rs.getDouble("amount"));
        t.setBalanceAfter(rs.getDouble("balance_after"));
        t.setDescription(rs.getString("description"));
        t.setTransactionDate(rs.getTimestamp("transaction_date"));
        return t;
    }
}
