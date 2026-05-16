package com.bank.service;

import com.bank.dao.*;
import com.bank.model.*;

import java.util.List;

/**
 * Bank Service Class
 * Contains all business logic: deposit, withdraw, transfer, etc.
 */
public class BankService {

    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private TransactionDAO transactionDAO;
    private UserDAO userDAO;

    public BankService() {
        this.accountDAO = new AccountDAO();
        this.customerDAO = new CustomerDAO();
        this.transactionDAO = new TransactionDAO();
        this.userDAO = new UserDAO();
    }

    // ─── AUTH ────────────────────────────────────────────────────────

    public int login(String username, String password) {
        return userDAO.login(username, password);
    }

    public String getUserRole(int userId) {
        return userDAO.getUserRole(userId);
    }

    public boolean registerCustomer(String username, String password,
                                     String fullName, String email, String phone, String address) {
        if (userDAO.usernameExists(username)) {
            System.out.println("✘ Username already taken.");
            return false;
        }
        int userId = userDAO.registerUser(username, password, "customer");
        if (userId == -1) return false;

        Customer customer = new Customer(userId, fullName, email, phone, address);
        return customerDAO.createCustomer(customer);
    }

    // ─── ACCOUNT ─────────────────────────────────────────────────────

    public Account openAccount(int customerId, String accountType, double initialDeposit) {
        if (initialDeposit < 500) {
            System.out.println("✘ Minimum initial deposit is ₹500.");
            return null;
        }
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setAccountType(accountType);
        account.setBalance(initialDeposit);

        if (accountDAO.createAccount(account)) {
            // Record initial deposit as a transaction
            Transaction txn = new Transaction(account.getAccountNumber(), "deposit",
                    initialDeposit, initialDeposit, "Initial deposit");
            transactionDAO.recordTransaction(txn);
            System.out.println("✔ Account opened: " + account.getAccountNumber());
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
        Account acc = accountDAO.getAccount(accountNumber);
        if (acc == null) {
            System.out.println("✘ Account not found.");
            return false;
        }
        if (acc.getBalance() > 0) {
            System.out.println("✘ Cannot close account with remaining balance. Please withdraw ₹" + acc.getBalance() + " first.");
            return false;
        }
        return accountDAO.closeAccount(accountNumber);
    }

    // ─── TRANSACTIONS ─────────────────────────────────────────────────

    public boolean deposit(String accountNumber, double amount) {
        if (amount <= 0) { System.out.println("✘ Invalid deposit amount."); return false; }

        Account acc = accountDAO.getAccount(accountNumber);
        if (acc == null || !acc.getStatus().equals("active")) {
            System.out.println("✘ Account not found or inactive.");
            return false;
        }

        double newBalance = acc.getBalance() + amount;
        if (accountDAO.updateBalance(accountNumber, newBalance)) {
            Transaction txn = new Transaction(accountNumber, "deposit", amount, newBalance, "Cash deposit");
            transactionDAO.recordTransaction(txn);
            System.out.printf("✔ ₹%.2f deposited. New balance: ₹%.2f%n", amount, newBalance);
            return true;
        }
        return false;
    }

    public boolean withdraw(String accountNumber, double amount) {
        if (amount <= 0) { System.out.println("✘ Invalid amount."); return false; }

        Account acc = accountDAO.getAccount(accountNumber);
        if (acc == null || !acc.getStatus().equals("active")) {
            System.out.println("✘ Account not found or inactive.");
            return false;
        }
        if (acc.getBalance() < amount) {
            System.out.printf("✘ Insufficient balance. Available: ₹%.2f%n", acc.getBalance());
            return false;
        }

        double newBalance = acc.getBalance() - amount;
        if (accountDAO.updateBalance(accountNumber, newBalance)) {
            Transaction txn = new Transaction(accountNumber, "withdrawal", amount, newBalance, "Cash withdrawal");
            transactionDAO.recordTransaction(txn);
            System.out.printf("✔ ₹%.2f withdrawn. New balance: ₹%.2f%n", amount, newBalance);
            return true;
        }
        return false;
    }

    public boolean transfer(String fromAccount, String toAccount, double amount) {
        if (amount <= 0) { System.out.println("✘ Invalid transfer amount."); return false; }

        Account from = accountDAO.getAccount(fromAccount);
        Account to = accountDAO.getAccount(toAccount);

        if (from == null || !from.getStatus().equals("active")) {
            System.out.println("✘ Source account not found or inactive.");
            return false;
        }
        if (to == null || !to.getStatus().equals("active")) {
            System.out.println("✘ Destination account not found or inactive.");
            return false;
        }
        if (from.getBalance() < amount) {
            System.out.printf("✘ Insufficient balance. Available: ₹%.2f%n", from.getBalance());
            return false;
        }

        double newFromBalance = from.getBalance() - amount;
        double newToBalance = to.getBalance() + amount;

        if (accountDAO.updateBalance(fromAccount, newFromBalance) &&
            accountDAO.updateBalance(toAccount, newToBalance)) {

            transactionDAO.recordTransaction(new Transaction(fromAccount, "transfer",
                    amount, newFromBalance, "Transfer to " + toAccount));
            transactionDAO.recordTransaction(new Transaction(toAccount, "transfer",
                    amount, newToBalance, "Transfer from " + fromAccount));

            System.out.printf("✔ ₹%.2f transferred from %s to %s%n", amount, fromAccount, toAccount);
            return true;
        }
        return false;
    }

    // ─── STATEMENTS ──────────────────────────────────────────────────

    public List<Transaction> getMiniStatement(String accountNumber) {
        return transactionDAO.getLastNTransactions(accountNumber, 5);
    }

    public List<Transaction> getFullStatement(String accountNumber) {
        return transactionDAO.getTransactionsByAccount(accountNumber);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    // ─── CUSTOMERS ───────────────────────────────────────────────────

    public Customer getCustomerByUserId(int userId) {
        return customerDAO.getCustomerByUserId(userId);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
}
