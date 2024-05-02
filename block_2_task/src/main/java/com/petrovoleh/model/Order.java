package com.petrovoleh.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId; // Ідентифікатор замовлення

    private Date orderDate; // Дата замовлення

    private String client; // Ім'я клієнта

    private int amount; // Сума замовлення

    @ElementCollection
    @Column(name = "items", columnDefinition = "jsonb")
    private List<String> items; // Список товарів

    // Constructors, getters, setters
    public Order() {}

    public Order(int orderId, Date orderDate, String client, int amount, List<String> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.client = client;
        this.amount = amount;
        this.items = items;
    }

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

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
