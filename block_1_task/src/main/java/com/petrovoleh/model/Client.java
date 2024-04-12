package com.petrovoleh.model;

/**
 * Цей клас представляє модель клієнта з іменем та електронною поштою.
 */
public class Client {
    private String name; // Ім'я клієнта
    private String email; // Електронна пошта клієнта

    /**
     * Створює порожній об'єкт клієнта.
     */
    public Client() {}

    /**
     * Створює об'єкт клієнта з вказаним ім'ям та електронною поштою.
     * @param name ім'я клієнта
     * @param email електронна пошта клієнта
     */
    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /* Гетери і сетери */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
