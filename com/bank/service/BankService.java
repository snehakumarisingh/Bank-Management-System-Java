package com.bank.service;

import com.bank.dao.*;
import com.bank.model.*;
import com.bank.util.DBConnection;

import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public class BankService {

    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private TransactionDAO transactionDAO;
    private UserDAO userDAO;

    public BankService() {
        accountDAO = new AccountDAO();
        customerDAO = new CustomerDAO();
        transactionDAO = new TransactionDAO();
        userDAO = new UserDAO();
    }

    public int login(String username, String password) {
        return userDAO.login(username, password);
    }

    public String getUserRole(int userId) {
        return userDAO.getUserRole(userId);
    }

    public boolean registerCustomer(String username, String password,
                                    String fullName, String email,
                                    String phone, String address) {

        if (userDAO.usernameExists(username)) {
            System.out.println("Username already taken.");
            return false;
        }

        int userId = userDAO.registerUser(username, password, "customer");

        if (userId == -1) {
            return false;
        }

        Customer customer = new Customer(
                userId, fullName, email, phone, address);

        return customerDAO.createCustomer(customer);
    }

    public Account openAccount(int customerId, String accountType,
                               double initialDeposit) {

        if (initialDeposit < 500) {
            System.out.println("Minimum initial deposit is Rs. 500.");
            return null;
        }

        Account account = new Account();
        account.setCustomerId(customerId);
        account.setAccountType(accountType);
        account.setBalance(initialDeposit);

        if (accountDAO.createAccount(account)) {

            Transaction txn = new Transaction(
                    account.getAccountNumber(),
                    "deposit",
                    initialDeposit,
                    initialDeposit,
                    "Initial deposit"
            );

            transactionDAO.recordTransaction(txn);

            System.out.println("Account opened: "
                    + account.getAccountNumber());

            return account;
        }

        return null;
    }

    public Account getAccount(String accountNumber) {
        return accountDAO.getAccount(accountNumber);
    }

    public List<Account> getCustomerAccounts(int customerId) {
        return accountDAO.getAccountsByCustomer(customerId);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public boolean closeAccount(String accountNumber) {

        Account account = accountDAO.getAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
            return false;
        }

        if (account.getBalance() > 0) {
            System.out.println(
                    "Cannot close account. Please withdraw the remaining balance first.");
            return false;
        }

        return accountDAO.closeAccount(accountNumber);
    }

    public boolean deposit(String accountNumber, double amount) {

        if (amount <= 0) {
            System.out.println("Invalid deposit amount.");
            return false;
        }

        Account account = accountDAO.getAccount(accountNumber);

        if (account == null
                || !"active".equals(account.getStatus())) {
            System.out.println("Account not found or inactive.");
            return false;
        }

        double newBalance = account.getBalance() + amount;

        if (accountDAO.updateBalance(accountNumber, newBalance)) {

            Transaction txn = new Transaction(
                    accountNumber,
                    "deposit",
                    amount,
                    newBalance,
                    "Cash deposit"
            );

            transactionDAO.recordTransaction(txn);

            System.out.printf(
                    "Rs. %.2f deposited. New balance: Rs. %.2f%n",
                    amount, newBalance);

            return true;
        }

        return false;
    }

    public boolean withdraw(String accountNumber, double amount) {

        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
            return false;
        }

        Account account = accountDAO.getAccount(accountNumber);

        if (account == null
                || !"active".equals(account.getStatus())) {
            System.out.println("Account not found or inactive.");
            return false;
        }

        if (account.getBalance() < amount) {
            System.out.printf(
                    "Insufficient balance. Available balance: Rs. %.2f%n",
                    account.getBalance());
            return false;
        }

        double newBalance = account.getBalance() - amount;

        if (accountDAO.updateBalance(accountNumber, newBalance)) {

            Transaction txn = new Transaction(
                    accountNumber,
                    "withdrawal",
                    amount,
                    newBalance,
                    "Cash withdrawal"
            );

            transactionDAO.recordTransaction(txn);

            System.out.printf(
                    "Rs. %.2f withdrawn. New balance: Rs. %.2f%n",
                    amount, newBalance);

            return true;
        }

        return false;
    }

    public boolean transfer(String fromAccount, String toAccount,
                            double amount) {

        if (amount <= 0) {
            System.out.println("Invalid transfer amount.");
            return false;
        }

        if (fromAccount == null || toAccount == null
                || fromAccount.equals(toAccount)) {
            System.out.println(
                    "Source and destination must be different accounts.");
            return false;
        }

        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.out.println("Database connection failed.");
            return false;
        }

        try {
            conn.setAutoCommit(false);

            Account from = accountDAO.getAccount(fromAccount);
            Account to = accountDAO.getAccount(toAccount);

            if (from == null || to == null
                    || !"active".equals(from.getStatus())
                    || !"active".equals(to.getStatus())) {

                conn.rollback();
                System.out.println("Account not found or inactive.");
                return false;
            }

            if (from.getBalance() < amount) {
                conn.rollback();
                System.out.println("Insufficient balance.");
                return false;
            }

            boolean updated = accountDAO.transferBalance(
                    fromAccount, toAccount, amount);

            if (!updated) {
                conn.rollback();
                System.out.println("Transfer failed.");
                return false;
            }

            double fromBalance = from.getBalance() - amount;
            double toBalance = to.getBalance() + amount;

            Transaction debit = new Transaction(
                    fromAccount,
                    "transfer",
                    amount,
                    fromBalance,
                    "Transfer to " + toAccount
            );

            Transaction credit = new Transaction(
                    toAccount,
                    "transfer",
                    amount,
                    toBalance,
                    "Transfer from " + fromAccount
            );

            boolean debitRecorded =
                    transactionDAO.recordTransaction(debit);

            boolean creditRecorded = debitRecorded
                    && transactionDAO.recordTransaction(credit);

            if (!debitRecorded || !creditRecorded) {
                conn.rollback();
                System.out.println(
                        "Transaction recording failed. Transfer cancelled.");
                return false;
            }

            conn.commit();

            System.out.printf(
                    "Transfer successful: Rs. %.2f from %s to %s%n",
                    amount, fromAccount, toAccount);

            return true;

        } catch (SQLException e) {

            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }

            System.out.println("Transfer failed: " + e.getMessage());
            return false;

        } finally {

            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(
                        "Could not reset auto-commit: " + e.getMessage());
            }
        }
    }

    public List<Transaction> getMiniStatement(String accountNumber) {
        return transactionDAO.getLastNTransactions(accountNumber, 5);
    }

    public List<Transaction> getFullStatement(String accountNumber) {
        return transactionDAO.getTransactionsByAccount(accountNumber);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    public Customer getCustomerByUserId(int userId) {
        return customerDAO.getCustomerByUserId(userId);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
}