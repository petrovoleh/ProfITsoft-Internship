package com.petrovoleh.controller;

import com.petrovoleh.model.Client;
import com.petrovoleh.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {


    private final ClientService service;

    @Autowired
    public ClientController(ClientService service) {
        this.service = service;
    }
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = service.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Client> getClientByName(@PathVariable String name) {
        Client client = service.getClientByName(name);
        if (client != null) {
            return ResponseEntity.ok(client);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client createdClient = service.createClient(client);
        if (createdClient != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PutMapping("/{name}")
    public ResponseEntity<Client> updateClient(@PathVariable String name, @RequestBody Client updatedClient) {
        Client client = service.updateClient(name, updatedClient);
        if (client != null) {
            return ResponseEntity.ok(client);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteClient(@PathVariable String name) {
        boolean deleted = service.deleteClient(name);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}