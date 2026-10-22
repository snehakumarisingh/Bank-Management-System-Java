package com.bank;

import com.bank.model.*;
import com.bank.service.BankService;

import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static BankService service = new BankService();

    public static void main(String[] args) {

        printBanner();

        while (true) {
            System.out.println("\n==============================");
            System.out.println("          MAIN MENU");
            System.out.println("==============================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    loginFlow();
                    break;

                case 2:
                    registerFlow();
                    break;

                case 3:
                    System.out.println(
                            "Thank you for using JavaBank. Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    static void loginFlow() {

        System.out.print("\nUsername: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        int userId = service.login(username, password);

        if (userId == -1) {
            System.out.println(
                    "Invalid credentials. Please try again.");
            return;
        }

        String role = service.getUserRole(userId);

        System.out.println("Login successful! Welcome, " + username);

        if ("admin".equals(role)) {
            adminMenu();
        } else {
            Customer customer = service.getCustomerByUserId(userId);

            if (customer != null) {
                customerMenu(customer);
            } else {
                System.out.println("Customer details not found.");
            }
        }
    }

    static void registerFlow() {

        System.out.println("\nNew Customer Registration");

        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        System.out.print("Full Name: ");
        String fullName = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Phone: ");
        String phone = sc.nextLine().trim();

        System.out.print("Address: ");
        String address = sc.nextLine().trim();

        boolean registered = service.registerCustomer(
                username, password, fullName,
                email, phone, address);

        if (registered) {
            System.out.println(
                    "Registration successful! You can now login.");
        } else {
            System.out.println(
                    "Registration failed. Username may already exist.");
        }
    }

    static void adminMenu() {

        while (true) {
            System.out.println("\n==============================");
            System.out.println("          ADMIN PANEL");
            System.out.println("==============================");
            System.out.println("1. View All Customers");
            System.out.println("2. View All Accounts");
            System.out.println("3. View All Transactions");
            System.out.println("4. Close an Account");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");

            switch (getIntInput()) {
                case 1:
                    List<Customer> customers =
                            service.getAllCustomers();

                    System.out.println(
                            "\nAll Customers (" + customers.size() + ")");

                    customers.forEach(System.out::println);
                    break;

                case 2:
                    List<Account> accounts =
                            service.getAllAccounts();

                    System.out.println(
                            "\nAll Accounts (" + accounts.size() + ")");

                    accounts.forEach(System.out::println);
                    break;

                case 3:
                    List<Transaction> transactions =
                            service.getAllTransactions();

                    System.out.println(
                            "\nAll Transactions ("
                                    + transactions.size() + ")");

                    transactions.forEach(System.out::println);
                    break;

                case 4:
                    System.out.print("Account Number to close: ");
                    String accountNumber = sc.nextLine().trim();

                    boolean closed =
                            service.closeAccount(accountNumber);

                    if (closed) {
                        System.out.println("Account closed successfully.");
                    } else {
                        System.out.println("Failed to close account.");
                    }
                    break;

                case 5:
                    System.out.println("Logged out.");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    static void customerMenu(Customer customer) {

        while (true) {
            System.out.println("\n==============================");
            System.out.println(
                    "Welcome, " + customer.getFullName());
            System.out.println("==============================");
            System.out.println("1. Open New Account");
            System.out.println("2. View My Accounts");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Transfer Funds");
            System.out.println("6. Mini Statement");
            System.out.println("7. Full Statement");
            System.out.println("8. Check Balance");
            System.out.println("9. Logout");
            System.out.print("Enter choice: ");

            switch (getIntInput()) {
                case 1:
                    openAccountFlow(customer);
                    break;

                case 2:
                    List<Account> myAccounts =
                            service.getCustomerAccounts(
                                    customer.getId());

                    if (myAccounts.isEmpty()) {
                        System.out.println("No accounts found.");
                    } else {
                        myAccounts.forEach(System.out::println);
                    }
                    break;

                case 3:
                    depositFlow();
                    break;

                case 4:
                    withdrawFlow();
                    break;

                case 5:
                    transferFlow();
                    break;

                case 6:
                    miniStatementFlow();
                    break;

                case 7:
                    fullStatementFlow();
                    break;

                case 8:
                    checkBalanceFlow();
                    break;

                case 9:
                    System.out.println("Logged out.");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    static void openAccountFlow(Customer customer) {

        System.out.println("Account Type: 1. Savings  2. Current");
        System.out.print("Enter choice: ");

        int choice = getIntInput();

        if (choice != 1 && choice != 2) {
            System.out.println("Invalid account type.");
            return;
        }

        String type = choice == 1 ? "savings" : "current";

        System.out.print("Initial Deposit (minimum Rs. 500): ");
        double amount = getDoubleInput();

        if (amount < 500) {
            System.out.println("Minimum deposit is Rs. 500.");
            return;
        }

        service.openAccount(customer.getId(), type, amount);
    }

    static void depositFlow() {

        System.out.print("Account Number: ");
        String accountNumber = sc.nextLine().trim();

        System.out.print("Amount: ");
        double amount = getDoubleInput();

        service.deposit(accountNumber, amount);
    }

    static void withdrawFlow() {

        System.out.print("Account Number: ");
        String accountNumber = sc.nextLine().trim();

        System.out.print("Amount: ");
        double amount = getDoubleInput();

        service.withdraw(accountNumber, amount);
    }

    static void transferFlow() {

        System.out.print("From Account: ");
        String fromAccount = sc.nextLine().trim();

        System.out.print("To Account: ");
        String toAccount = sc.nextLine().trim();

        System.out.print("Amount: ");
        double amount = getDoubleInput();

        service.transfer(fromAccount, toAccount, amount);
    }

    static void miniStatementFlow() {

        System.out.print("Account Number: ");
        String accountNumber = sc.nextLine().trim();

        List<Transaction> transactions =
                service.getMiniStatement(accountNumber);

        System.out.println("\nLast 5 Transactions");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    static void fullStatementFlow() {

        System.out.print("Account Number: ");
        String accountNumber = sc.nextLine().trim();

        List<Transaction> transactions =
                service.getFullStatement(accountNumber);

        System.out.println(
                "\nFull Statement ("
                        + transactions.size() + " transactions)");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    static void checkBalanceFlow() {

        System.out.print("Account Number: ");
        String accountNumber = sc.nextLine().trim();

        Account account = service.getAccount(accountNumber);

        if (account != null) {
            System.out.printf(
                    "Current Balance: Rs. %.2f%n",
                    account.getBalance());
        } else {
            System.out.println("Account not found.");
        }
    }

    static int getIntInput() {

        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static double getDoubleInput() {

        try {
            return Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static void printBanner() {

        System.out.println("==============================");
        System.out.println("   JavaBank Management System");
        System.out.println("      Java + MySQL");
        System.out.println("==============================");
    }
}