package com.petrovoleh.model;

public class OrderResponse {
    private final Order order;
    private final Client client;

    public OrderResponse(Order order, Client client) {
        this.order = order;
        this.client = client;
    }

    public Order getOrder() {
        return order;
    }

    public Client getClient() {
        return client;
    }
}