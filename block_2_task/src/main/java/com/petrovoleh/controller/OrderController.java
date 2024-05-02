package com.petrovoleh.controller;

import com.petrovoleh.model.Client;
import com.petrovoleh.model.Order;
import com.petrovoleh.model.Order;
import com.petrovoleh.model.OrderResponse;
import com.petrovoleh.service.ClientService;
import com.petrovoleh.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;
    private final ClientService clientService;

    @Autowired
    public OrderController(OrderService service, ClientService clientService) {
        this.service = service;
        this.clientService = clientService;
    }
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = service.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable int id) {
        Order order = service.getOrderById(id);

        if (order != null) {
            Client client = clientService.getClientByName(order.getClient());
            if (client != null) {
                OrderResponse orderResponse = new OrderResponse(order, client);
                return ResponseEntity.ok(orderResponse);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = service.createOrder(order);
        if (createdOrder != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable int id, @RequestBody Order updatedOrder) {
        Order order = service.updateOrder(id, updatedOrder);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) {
        boolean deleted = service.deleteOrder(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
