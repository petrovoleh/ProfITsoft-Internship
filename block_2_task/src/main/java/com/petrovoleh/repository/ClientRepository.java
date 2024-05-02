package com.petrovoleh.repository;

import com.petrovoleh.model.Client;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @PostConstruct
    public void initialize() {
        // Check if the clients table exists, create it if not
        if (!tableExists("clients")) {
            createClientsTable();
        }
    }

    private boolean tableExists(String tableName) {
        String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, tableName);
    }

    private void createClientsTable() {
        String sql = "CREATE TABLE clients (name VARCHAR(100) PRIMARY KEY, email VARCHAR(100) NOT NULL)";
        jdbcTemplate.execute(sql);
    }
    public List<Client> getAllClients() {
        String sql = "SELECT * FROM clients";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Client.class));
    }

    public Client getClientByName(String name) {
        String sql = "SELECT * FROM clients WHERE name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{name}, new BeanPropertyRowMapper<>(Client.class));
        } catch (EmptyResultDataAccessException e) {
            return null; // Return null if no client with the specified name exists
        }
    }

    public Client createClient(Client client) {
        String sql = "INSERT INTO clients (name, email) VALUES (?, ?)";
        int rowsInserted = jdbcTemplate.update(sql, client.getName(), client.getEmail());
        if (rowsInserted > 0) {
            return client;
        }
        return null; // Return null if insertion failed
    }

    public Client updateClient(Client client) {
        String sql = "UPDATE clients SET email = ? WHERE name = ?";
        int rowsUpdated = jdbcTemplate.update(sql, client.getEmail(), client.getName());
        if (rowsUpdated > 0) {
            return client;
        }
        return null; // Return null if update failed
    }

    public boolean deleteClient(String name) {
        String sql = "DELETE FROM clients WHERE name = ?";
        int rowsDeleted = jdbcTemplate.update(sql, name);
        return rowsDeleted > 0; // Return true if deletion successful, false otherwise
    }
}
