package com.petrovoleh.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrovoleh.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Клас OrderRepository забезпечує доступ до даних про замовлення в базі даних.
 */
@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initialize() {
        if (!tableExists()) {
            createOrdersTable();
        }
    }

    private boolean tableExists() {
        String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, "orders"));
    }

    private void createOrdersTable() {
        String sql = "CREATE TABLE orders (id SERIAL PRIMARY KEY, client VARCHAR(100)REFERENCES clients(name), amount INTEGER, items JSONB, order_date TIMESTAMP)";
        jdbcTemplate.execute(sql);
    }

    /**
     * Отримує замовлення за його ідентифікатором.
     *
     * @param id Ідентифікатор замовлення.
     * @return Об'єкт замовлення, або null, якщо замовлення з таким ідентифікатором не знайдено.
     */
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
            return null;
        }
    }

    /**
     * Десеріалізує JSON рядок в список об'єктів.
     *
     * @param itemsJson JSON рядок.
     * @return Список об'єктів.
     */
    private List<String> itemsFromJson(String itemsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(itemsJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Серіалізує список об'єктів у JSON рядок.
     *
     * @param order Об'єкт замовлення.
     * @return JSON рядок.
     */
    private String itemsToJson(Order order) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(order.getItems());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Створює нове замовлення.
     *
     * @param order Об'єкт замовлення для створення.
     * @return Створене замовлення.
     */
    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (order_date, client, amount, items) VALUES (?, ?, ?, ?::jsonb) RETURNING id";
        int id = jdbcTemplate.queryForObject(sql, Integer.class, order.getOrderDate(), order.getClient(),
                order.getAmount(), itemsToJson(order));
        order.setOrderId(id);
        return order;
    }

    /**
     * Оновлює існуюче замовлення.
     *
     * @param order Об'єкт замовлення для оновлення.
     * @return Оновлене замовлення, або null, якщо оновлення не вдалося.
     */
    public Order updateOrder(Order order) {
        String sql = "UPDATE orders SET order_date = ?, client = ?, amount = ?, items = ?::jsonb WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, order.getOrderDate(), order.getClient(),
                order.getAmount(), itemsToJson(order), order.getOrderId());
        if (rowsUpdated > 0) {
            return order;
        }
        return null;
    }

    /**
     * Видаляє замовлення за ідентифікатором.
     *
     * @param id Ідентифікатор замовлення для видалення.
     * @return true, якщо видалення вдале, false - в іншому випадку.
     */
    public boolean deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        return rowsDeleted > 0;
    }

    /**
     * Повертає сторінку замовлень клієнта за його ім'ям.
     *
     * @param clientName Ім'я клієнта.
     * @param page       Номер сторінки.
     * @param size       Кількість записів на сторінці.
     * @return Сторінка замовлень.
     */
    public Page<Order> findByClient(String clientName, int page, int size) {
        String countQuery = "SELECT COUNT(*) FROM orders WHERE client = ?";
        int total = jdbcTemplate.queryForObject(countQuery, Integer.class, clientName);

        int offset = page * size;

        String dataQuery = "SELECT * FROM orders WHERE client = ? LIMIT ? OFFSET ?";
        List<Order> orders = jdbcTemplate.query(dataQuery, new Object[]{clientName, size, offset}, (rs, rowNum) -> {
            Order order = new Order();
            order.setOrderId(rs.getInt("id"));
            order.setOrderDate(rs.getDate("order_date"));
            order.setClient(rs.getString("client"));
            order.setAmount(rs.getInt("amount"));
            String itemsJson = rs.getString("items");
            order.setItems(itemsFromJson(itemsJson));
            return order;
        });

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(orders, pageable, total);
    }

    /**
     * Повертає список всіх замовлень клієнта за його ім'ям.
     *
     * @param clientName Ім'я клієнта.
     * @return Список замовлень клієнта.
     */
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