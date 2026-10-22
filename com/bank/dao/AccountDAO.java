package com.bank.dao;

import com.bank.model.Account;
import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountDAO {

    private Connection conn;

    public AccountDAO() {
        conn = DBConnection.getConnection();
    }

    private String generateAccountNumber() {
        Random rand = new Random();
        return "ACC" + (1000000000L
                + (long) (rand.nextDouble() * 9000000000L));
    }

    public boolean createAccount(Account account) {

        String accountNumber = generateAccountNumber();
        account.setAccountNumber(accountNumber);

        String sql = "INSERT INTO accounts "
                + "(account_number, customer_id, account_type, balance, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getAccountNumber());
            ps.setInt(2, account.getCustomerId());
            ps.setString(3, account.getAccountType());
            ps.setDouble(4, account.getBalance());
            ps.setString(5, "active");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error creating account: " + e.getMessage());
            return false;
        }
    }

    public Account getAccount(String accountNumber) {

        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching account: " + e.getMessage());
        }

        return null;
    }

    public List<Account> getAccountsByCustomer(int customerId) {

        List<Account> accounts = new ArrayList<>();

        String sql = "SELECT * FROM accounts WHERE customer_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapAccount(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching accounts: " + e.getMessage());
        }

        return accounts;
    }

    public List<Account> getAllAccounts() {

        List<Account> accounts = new ArrayList<>();

        String sql = "SELECT a.*, c.full_name "
                + "FROM accounts a "
                + "JOIN customers c ON a.customer_id = c.id";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(mapAccount(rs));
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching all accounts: " + e.getMessage());
        }

        return accounts;
    }

    public boolean updateBalance(String accountNumber, double newBalance) {

        String sql = "UPDATE accounts SET balance = ? "
                + "WHERE account_number = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error updating balance: " + e.getMessage());
            return false;
        }
    }

    public boolean transferBalance(String fromAccount, String toAccount,
                                   double amount) throws SQLException {

        String debitSql = "UPDATE accounts SET balance = balance - ? "
                + "WHERE account_number = ? "
                + "AND status = 'active' AND balance >= ?";

        String creditSql = "UPDATE accounts SET balance = balance + ? "
                + "WHERE account_number = ? AND status = 'active'";

        try (PreparedStatement debit = conn.prepareStatement(debitSql);
             PreparedStatement credit = conn.prepareStatement(creditSql)) {

            debit.setDouble(1, amount);
            debit.setString(2, fromAccount);
            debit.setDouble(3, amount);

            if (debit.executeUpdate() != 1) {
                return false;
            }

            credit.setDouble(1, amount);
            credit.setString(2, toAccount);

            if (credit.executeUpdate() != 1) {
                return false;
            }

            return true;
        }
    }

    public boolean closeAccount(String accountNumber) {

        String sql = "UPDATE accounts SET status = 'closed' "
                + "WHERE account_number = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error closing account: " + e.getMessage());
            return false;
        }
    }

    private Account mapAccount(ResultSet rs) throws SQLException {

        Account account = new Account();

        account.setAccountNumber(rs.getString("account_number"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getDouble("balance"));
        account.setStatus(rs.getString("status"));

        return account;
    }
}