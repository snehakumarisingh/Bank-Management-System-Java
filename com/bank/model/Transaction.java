package com.bank.model;

import java.sql.Timestamp;

public class Transaction {

    private int id;
    private String accountNumber;
    private String transactionType;
    private double amount;
    private double balanceAfter;
    private String description;
    private Timestamp transactionDate;

    public Transaction() {
    }

    public Transaction(String accountNumber, String transactionType,
                       double amount, double balanceAfter,
                       String description) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Transaction[%s | Type: %s | Amount: Rs. %.2f | Balance After: Rs. %.2f | Date: %s]",
                accountNumber, transactionType, amount,
                balanceAfter, transactionDate
        );
    }
}