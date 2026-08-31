package com.bank.model;

/**
 * Customer Model Class
 * Represents a bank customer entity
 */
public class Customer {

    private int id;
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;

    public Customer() {}

    public Customer(int userId, String fullName, String email, String phone, String address) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return String.format("Customer[ID: %d | Name: %s | Email: %s | Phone: %s]",
                id, fullName, email, phone);
    }
}
