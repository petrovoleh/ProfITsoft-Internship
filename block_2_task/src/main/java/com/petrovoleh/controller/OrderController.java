package com.petrovoleh.controller;

import com.petrovoleh.model.*;
import com.petrovoleh.model.Order;
import com.petrovoleh.parser.parser.Parser;
import com.petrovoleh.service.ClientService;
import com.petrovoleh.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @PostMapping("/upload")
    public ResponseEntity<?> uploadOrders(@RequestBody List<Order> orders) {
        // Парсити JSON-файл і передавати список замовлень на обробку сервісу
        int successfulImports = service.saveOrders(orders);
        int failedImports = orders.size() - successfulImports;
        String response = "{\"successfulImports\": " + successfulImports + ", \"failedImports\": " + failedImports + "}";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/_report")
    public ResponseEntity<OrderResponse> postReport(@PathVariable int id) {

        return ResponseEntity.notFound().build();
    }
    @PostMapping("/_list")
    public ResponseEntity<List<Order>> postList(@RequestBody OrderListRequest request) {
        List<Order> orders = service.getOrdersByName(request.getName(), request.getPage(), request.getSize());


        return ResponseEntity.ok(orders);
    }
}
