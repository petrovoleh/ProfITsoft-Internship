package com.petrovoleh.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrovoleh.model.Order;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @PostConstruct
    public void initialize() {
        // Check if the orders table exists, create it if not
        if (!tableExists("orders")) {
            createOrdersTable();
        }
    }

    private boolean tableExists(String tableName) {
        String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, tableName);
    }

    private void createOrdersTable() {
        String sql = "CREATE TABLE orders (id SERIAL PRIMARY KEY, client VARCHAR(100), amount INTEGER, items JSONB, order_date TIMESTAMP, email VARCHAR(100) NOT NULL)";
        jdbcTemplate.execute(sql);
    }

    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class));
    }

    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(Order.class));
        } catch (EmptyResultDataAccessException e) {
            return null; // Return null if no order with the specified id exists
        }
    }

    public Order createOrder(Order order) {
        System.out.println("createOrder");
        String sql = "INSERT INTO orders (order_date, client, amount, items) VALUES (?, ?, ?, ?::jsonb)";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String itemsJson = objectMapper.writeValueAsString(order.getItems());
            int rowsInserted = jdbcTemplate.update(sql, order.getOrderDate(), order.getClient(),
                    order.getAmount(), itemsJson);
            if (rowsInserted > 0) {
                return order;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle serialization error
        }
        return null; // Return null if insertion failed
    }

    public Order updateOrder(Order order) {
        String sql = "UPDATE orders SET order_date = ?, client = ?, amount = ?, items = ?::jsonb WHERE orderId = ?";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String itemsJson = objectMapper.writeValueAsString(order.getItems());
            int rowsUpdated = jdbcTemplate.update(sql, order.getOrderDate(), order.getClient(),
                    order.getAmount(), itemsJson,
                    order.getOrderId());
            if (rowsUpdated > 0) {
                return order;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle serialization error
        }
        return null; // Return null if update failed
    }

    public boolean deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        return rowsDeleted > 0; // Return true if deletion successful, false otherwise
    }
}
