package com.petrovoleh.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Цей клас представляє модель замовлення з ідентифікатором, датою замовлення, клієнтом, сумою та списком товарів.
 */
public class Order {
    private int orderId; // Ідентифікатор замовлення
    private Date orderDate; // Дата замовлення
    private String client; // Ім'я клієнта
    private int amount; // Сума замовлення
    private List<String> items; // Список товарів

    /**
     * Створює порожній об'єкт замовлення.
     */
    public Order() {}

    /**
     * Створює об'єкт замовлення з вказаними параметрами.
     * @param orderId ідентифікатор замовлення
     * @param orderDate дата замовлення
     * @param amount сума замовлення
     * @param client ім'я клієнта
     */
    public Order(int orderId, Date orderDate, int amount, String client) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.items = new ArrayList<>();
        this.client = client;
    }

    /* Гетери і сетери */
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public void addItem(String item) {
        items.add(item);
    }
}
