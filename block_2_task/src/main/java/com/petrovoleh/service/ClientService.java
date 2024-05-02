package com.petrovoleh.service;

import com.petrovoleh.model.Client;
import com.petrovoleh.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.getAllClients();
    }

    public Client getClientByName(String name) {
        return clientRepository.getClientByName(name);
    }

    public Client createClient(Client client) {
        // Check if client with the same name already exists
        Client existingClient = clientRepository.getClientByName(client.getName());
        if (existingClient != null) {
            return null; // Client with the same name already exists
        }
        return clientRepository.createClient(client);
    }

    public Client updateClient(String name, Client updatedClient) {
        Client existingClient = clientRepository.getClientByName(name);
        if (existingClient != null) {
            updatedClient.setName(name);
            return clientRepository.updateClient(updatedClient);
        }
        return null; // Client not found
    }

    public boolean deleteClient(String name) {
        Client existingClient = clientRepository.getClientByName(name);
        if (existingClient != null) {
            clientRepository.deleteClient(name);
            return true;
        }
        return false; // Client not found
    }
}
