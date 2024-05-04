package com.petrovoleh.repository;

import com.petrovoleh.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Клас ClientRepository забезпечує доступ до даних клієнтів в базі даних.
 */
@Repository
public class ClientRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Ініціалізує репозиторій, перевіряючи наявність таблиці клієнтів та створюючи її, якщо не існує.
     */
    @PostConstruct
    public void initialize() {
        if (!tableExists()) {
            createClientsTable();
        }
    }

    /**
     * Перевіряє наявність таблиці клієнтів.
     *
     * @return true, якщо таблиця існує, false - в іншому випадку.
     */
    private boolean tableExists() {
        String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, "clients"));
    }

    /**
     * Створює таблицю клієнтів у базі даних.
     */
    private void createClientsTable() {
        String sql = "CREATE TABLE clients (name VARCHAR(100) PRIMARY KEY, email VARCHAR(100) NOT NULL)";
        jdbcTemplate.execute(sql);
    }

    /**
     * Отримує всіх клієнтів з бази даних.
     *
     * @return Список клієнтів.
     */
    public List<Client> getAllClients() {
        String sql = "SELECT * FROM clients";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Client.class));
    }

    /**
     * Отримує клієнта за ім'ям.
     *
     * @param name Ім'я клієнта.
     * @return Об'єкт клієнта, або null, якщо клієнт не знайдений.
     */
    public Client getClientByName(String name) {
        String sql = "SELECT * FROM clients WHERE name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{name}, new BeanPropertyRowMapper<>(Client.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Створює нового клієнта у базі даних.
     *
     * @param client Об'єкт клієнта для створення.
     * @return Створений клієнт.
     */
    public Client createClient(Client client) {
        String sql = "INSERT INTO clients (name, email) VALUES (?, ?)";
        int rowsInserted = jdbcTemplate.update(sql, client.getName(), client.getEmail());
        if (rowsInserted > 0) {
            return client;
        }
        return null;
    }

    /**
     * Оновлює дані існуючого клієнта у базі даних.
     *
     * @param client Об'єкт клієнта з оновленими даними.
     * @return Оновлений клієнт.
     */
    public Client updateClient(Client client) {
        String sql = "UPDATE clients SET email = ? WHERE name = ?";
        int rowsUpdated = jdbcTemplate.update(sql, client.getEmail(), client.getName());
        if (rowsUpdated > 0) {
            return client;
        }
        return null;
    }

    /**
     * Видаляє клієнта за ім'ям із бази даних.
     *
     * @param name Ім'я клієнта для видалення.
     * @return true, якщо клієнт був успішно видалений, false - в іншому випадку.
     */
    public boolean deleteClient(String name) {
        String sql = "DELETE FROM clients WHERE name = ?";
        int rowsDeleted = jdbcTemplate.update(sql, name);
        return rowsDeleted > 0;
    }
}
