package com.bank.dao;

import com.bank.model.Customer;
import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private Connection conn;

    public CustomerDAO() {
        conn = DBConnection.getConnection();
    }

    public boolean createCustomer(Customer customer) {

        String sql = "INSERT INTO customers "
                + "(user_id, full_name, email, phone, address) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, customer.getUserId());
            ps.setString(2, customer.getFullName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getAddress());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        customer.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error creating customer: " + e.getMessage());
        }

        return false;
    }

    public Customer getCustomerById(int id) {

        String sql = "SELECT * FROM customers WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching customer: " + e.getMessage());
        }

        return null;
    }

    public Customer getCustomerByUserId(int userId) {

        String sql = "SELECT * FROM customers WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching customer: " + e.getMessage());
        }

        return null;
    }

    public List<Customer> getAllCustomers() {

        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapCustomer(rs));
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error fetching all customers: " + e.getMessage());
        }

        return list;
    }

    public boolean updateCustomer(Customer customer) {

        String sql = "UPDATE customers SET full_name = ?, email = ?, "
                + "phone = ?, address = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error updating customer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCustomer(int id) {

        String sql = "DELETE FROM customers WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {

        Customer customer = new Customer();

        customer.setId(rs.getInt("id"));
        customer.setUserId(rs.getInt("user_id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));

        return customer;
    }
}