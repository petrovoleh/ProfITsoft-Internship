package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Client;
import org.example.model.Order;
import org.example.parser.JsonParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse clients
        List<Client> clients = JsonParser.parseClients(objectMapper, "./test_jsons/clients.json");
        System.out.println("Clients:");
        for (Client client : clients) {
            System.out.println(client.getName() + " - " + client.getEmail());
        }

        // Parse orders
        List<Order> orders = JsonParser.parseOrders(objectMapper, "./test_jsons/orders.json");
        System.out.println("\nOrders:");
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getOrderId() + ", Date: " + order.getOrderDate() + ", Amount: " + order.getAmount() + ", Items: " + order.getItems());
        }
    }
}