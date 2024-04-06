package org.example.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private int orderId;
    private Date orderDate;
    private String client;
    private int amount;
    private List<String> items;

    public Order() {}

    public Order(int orderId, Date orderDate, int amount, String client) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.items = new ArrayList<>();
        this.client = client;
    }

    // Getters and setters

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
