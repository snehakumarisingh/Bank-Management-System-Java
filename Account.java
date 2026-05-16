package com.bank.model;

/**
 * Account Model Class
 * Represents a bank account entity
 */
public class Account {

    private String accountNumber;
    private int customerId;
    private String accountType;
    private double balance;
    private String status;

    public Account() {}

    public Account(String accountNumber, int customerId, String accountType, double balance, String status) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Account[No: %s | Type: %s | Balance: ₹%.2f | Status: %s]",
                accountNumber, accountType, balance, status);
    }
}
