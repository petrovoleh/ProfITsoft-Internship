package com.petrovoleh.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrovoleh.model.Order;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        String sql = "CREATE TABLE orders (id SERIAL PRIMARY KEY, client VARCHAR(100)REFERENCES clients(name), amount INTEGER, items JSONB, order_date TIMESTAMP)";
        jdbcTemplate.execute(sql);
    }

    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
                Order order = new Order();
                order.setOrderId(rs.getInt("id"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setClient(rs.getString("client"));
                order.setAmount(rs.getInt("amount"));
                String itemsJson = rs.getString("items");
                order.setItems(itemsFromJson(itemsJson));
                return order;
            });
        } catch (EmptyResultDataAccessException e) {
            return null; // Return null if no order with the specified id exists
        }
    }
    private List<String> itemsFromJson(String itemsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(itemsJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String itemsToJson(Order order){
        ObjectMapper objectMapper = new ObjectMapper();
        String itemsJson = null;
        try {
            itemsJson = objectMapper.writeValueAsString(order.getItems());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return itemsJson;
    }
    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (order_date, client, amount, items) VALUES (?, ?, ?, ?::jsonb) RETURNING id";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, order.getOrderDate(), order.getClient(),
                order.getAmount(), itemsToJson(order));
        if (rowSet.next()) {
            int id = rowSet.getInt("id");
            order.setOrderId(id);
            System.out.println("Order created: " + order.getOrderId());
            return order;
        }

        return null; // Return null if insertion failed
    }

    public Order updateOrder(Order order) {
        String sql = "UPDATE orders SET order_date = ?, client = ?, amount = ?, items = ?::jsonb WHERE id = ?";

            int rowsUpdated = jdbcTemplate.update(sql, order.getOrderDate(), order.getClient(),
                    order.getAmount(), itemsToJson(order),
                    order.getOrderId());
            if (rowsUpdated > 0) {
                return order;
            }
        return null; // Return null if update failed
    }


    public boolean deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        return rowsDeleted > 0; // Return true if deletion successful, false otherwise
    }

    public Page<Order> findByClient(String clientName, int page, int size) {
        // Query to fetch the total count
        String countQuery = "SELECT COUNT(*) FROM orders WHERE client = ?";
        int total = jdbcTemplate.queryForObject(countQuery, Integer.class, clientName);

        // Calculate the offset based on the page number and size
        int offset = page * size;

        // Query to fetch the data for the current page
        String dataQuery = "SELECT * FROM orders WHERE client = ? LIMIT ? OFFSET ?";
        List<Order> orders = jdbcTemplate.query(dataQuery, new Object[]{clientName, size, offset},
                (rs, rowNum) -> {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("id"));
                    order.setOrderDate(rs.getDate("order_date"));
                    order.setClient(rs.getString("client"));
                    order.setAmount(rs.getInt("amount"));
                    String itemsJson = rs.getString("items");
                    order.setItems(itemsFromJson(itemsJson));
                    return order;
                });

        // Create a Pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Return a PageImpl instance with the fetched data and total count
        return new PageImpl<>(orders, pageable, total);
    }
    public List<Order> findAllByClient(String clientName) {
        String sql = "SELECT * FROM orders WHERE client = ?";
        return jdbcTemplate.query(sql, new Object[]{clientName}, (rs, rowNum) -> {
            Order order = new Order();
            order.setOrderId(rs.getInt("id"));
            order.setOrderDate(rs.getDate("order_date"));
            order.setClient(rs.getString("client"));
            order.setAmount(rs.getInt("amount"));
            String itemsJson = rs.getString("items");
            order.setItems(itemsFromJson(itemsJson));
            return order;
        });
    }
}
