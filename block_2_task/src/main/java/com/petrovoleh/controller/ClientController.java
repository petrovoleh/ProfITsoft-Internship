package com.petrovoleh.controller;

import com.petrovoleh.model.Client;
import com.petrovoleh.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Клас ClientController обробляє HTTP-запити, пов'язані з операціями клієнта.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    @Autowired
    public ClientController(ClientService service) {
        this.service = service;
    }

    /**
     * Отримує всіх клієнтів.
     *
     * @return ResponseEntity із списком клієнтів та HTTP-статусом OK у разі успіху, або HTTP-статусом NOT_FOUND, якщо клієнти не знайдені.
     */
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = service.getAllClients();
        return ResponseEntity.ok(clients);
    }

    /**
     * Отримує клієнта за ім'ям.
     *
     * @param name Ім'я клієнта для отримання.
     * @return ResponseEntity із клієнтом та HTTP-статусом OK у разі успіху, або HTTP-статусом NOT_FOUND, якщо клієнт не знайдений.
     */
    @GetMapping("/{name}")
    public ResponseEntity<Client> getClientByName(@PathVariable String name) {
        Client client = service.getClientByName(name);
        if (client != null) {
            return ResponseEntity.ok(client);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Створює нового клієнта.
     *
     * @param client Об'єкт клієнта для створення.
     * @return ResponseEntity із створеним клієнтом та HTTP-статусом CREATED у разі успіху, або HTTP-статусом CONFLICT, якщо клієнт вже існує.
     */
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client createdClient = service.createClient(client);
        if (createdClient != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Оновлює існуючого клієнта.
     *
     * @param name Ім'я клієнта для оновлення.
     * @param updatedClient Оновлений об'єкт клієнта.
     * @return ResponseEntity із оновленим клієнтом та HTTP-статусом OK у разі успіху, або HTTP-статусом NOT_FOUND, якщо клієнт не знайдений.
     */
    @PutMapping("/{name}")
    public ResponseEntity<Client> updateClient(@PathVariable String name, @RequestBody Client updatedClient) {
        Client client = service.updateClient(name, updatedClient);
        if (client != null) {
            return ResponseEntity.ok(client);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Видаляє клієнта за ім'ям.
     *
     * @param name Ім'я клієнта для видалення.
     * @return ResponseEntity з HTTP-статусом NO_CONTENT у разі успіху, або HTTP-статусом NOT_FOUND, якщо клієнт не знайдений.
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteClient(@PathVariable String name) {
        boolean deleted = service.deleteClient(name);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
