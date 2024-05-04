package com.petrovoleh.service;

import com.petrovoleh.model.Client;
import com.petrovoleh.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Клас ClientService забезпечує бізнес-логіку для операцій з клієнтами.
 */
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Отримує всіх клієнтів.
     *
     * @return Список всіх клієнтів.
     */
    public List<Client> getAllClients() {
        return clientRepository.getAllClients();
    }

    /**
     * Отримує клієнта за ім'ям.
     *
     * @param name Ім'я клієнта.
     * @return Об'єкт клієнта, або null, якщо клієнт не знайдений.
     */
    public Client getClientByName(String name) {
        return clientRepository.getClientByName(name);
    }

    /**
     * Створює нового клієнта.
     *
     * @param client Об'єкт клієнта для створення.
     * @return Створений клієнт, або null, якщо клієнт з таким ім'ям вже існує.
     */
    public Client createClient(Client client) {
        // Перевірка, чи існує клієнт з таким самим ім'ям
        Client existingClient = clientRepository.getClientByName(client.getName());
        if (existingClient != null) {
            return null; // Клієнт з таким ім'ям вже існує
        }
        return clientRepository.createClient(client);
    }

    /**
     * Оновлює дані клієнта.
     *
     * @param name         Ім'я клієнта для оновлення.
     * @param updatedClient Об'єкт клієнта з оновленими даними.
     * @return Оновлений клієнт, або null, якщо клієнт не знайдений.
     */
    public Client updateClient(String name, Client updatedClient) {
        Client existingClient = clientRepository.getClientByName(name);
        if (existingClient != null) {
            updatedClient.setName(name);
            return clientRepository.updateClient(updatedClient);
        }
        return null; // Клієнт не знайдений
    }

    /**
     * Видаляє клієнта за ім'ям.
     *
     * @param name Ім'я клієнта для видалення.
     * @return true, якщо клієнт був успішно видалений, false - в іншому випадку.
     */
    public boolean deleteClient(String name) {
        Client existingClient = clientRepository.getClientByName(name);
        if (existingClient != null) {
            clientRepository.deleteClient(name);
            return true;
        }
        return false; // Клієнт не знайдений
    }
}
