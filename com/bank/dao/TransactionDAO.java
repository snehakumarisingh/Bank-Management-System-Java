package com.bank.dao;

import com.bank.model.Transaction;
import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private Connection conn;

    public TransactionDAO() {
        conn = DBConnection.getConnection();
    }

    public boolean recordTransaction(Transaction txn) {

        String sql = "INSERT INTO transactions "
                + "(account_number, transaction_type, amount, "
                + "balance_after, description) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txn.getAccountNumber());
            ps.setString(2, txn.getTransactionType());
            ps.setDouble(3, txn.getAmount());
            ps.setDouble(4, txn.getBalanceAfter());
            ps.setString(5, txn.getDescription());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error recording transaction: " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> getTransactionsByAccount(
            String accountNumber) {

        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM transactions "
                + "WHERE account_number = ? "
                + "ORDER BY transaction_date DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching transactions: " + e.getMessage());
        }

        return list;
    }

    public List<Transaction> getLastNTransactions(
            String accountNumber, int n) {

        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM transactions "
                + "WHERE account_number = ? "
                + "ORDER BY transaction_date DESC LIMIT ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);
            ps.setInt(2, n);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching transactions: " + e.getMessage());
        }

        return list;
    }

    public List<Transaction> getAllTransactions() {

        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM transactions "
                + "ORDER BY transaction_date DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapTransaction(rs));
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching all transactions: " + e.getMessage());
        }

        return list;
    }

    private Transaction mapTransaction(ResultSet rs)
            throws SQLException {

        Transaction transaction = new Transaction();

        transaction.setId(rs.getInt("id"));
        transaction.setAccountNumber(rs.getString("account_number"));
        transaction.setTransactionType(
                rs.getString("transaction_type"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setBalanceAfter(rs.getDouble("balance_after"));
        transaction.setDescription(rs.getString("description"));
        transaction.setTransactionDate(
                rs.getTimestamp("transaction_date"));

        return transaction;
    }
}