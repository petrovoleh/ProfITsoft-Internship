package com.petrovoleh.controller;

import com.petrovoleh.model.*;
import com.petrovoleh.model.Order;
import com.petrovoleh.service.ClientService;
import com.petrovoleh.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ResponseEntity<byte[]> generateReport(@RequestBody OrderListRequest request) {
        List<Order> allOrders = service.getAllOrdersByName(request.getName());
        // Generate CSV content
        String csvContent = generateCsvContent(allOrders);

        // Set headers for CSV response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "report.csv");

        // Convert CSV content to byte array
        byte[] csvBytes = csvContent.getBytes();

        // Return CSV file as response
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
    @PostMapping("/_list")
    public ResponseEntity<OrderListResponse> postList(@RequestBody OrderListRequest request) {
        Page<Order> ordersPage = service.getOrdersByName(request.getName(), request.getPage(), request.getSize());

        List<Order> orders = ordersPage.getContent(); // Get list of orders for the current page
        int totalPages = ordersPage.getTotalPages(); // Get total number of pages

        OrderListResponse response = new OrderListResponse(orders, totalPages);
        return ResponseEntity.ok(response);
    }


    private String generateCsvContent(List<Order> orders) {
        StringBuilder csvContent = new StringBuilder();

        // Append CSV header
        csvContent.append("Order ID,Order Date,Client,Amount,Items\n");

        // Iterate over each order and append CSV rows
        for (Order order : orders) {
            // Append order details as CSV row
            csvContent.append(order.getOrderId()).append(",")
                    .append(order.getOrderDate()).append(",")
                    .append(order.getClient()).append(",")
                    .append(order.getAmount()).append(",")
                    .append(formatItems(order.getItems())).append("\n");
        }

        return csvContent.toString();
    }

    // Method to format items list as a comma-separated string
    private String formatItems(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }

        StringBuilder formattedItems = new StringBuilder();
        for (String item : items) {
            formattedItems.append(item).append(", ");
        }

        // Remove trailing comma and space
        formattedItems.delete(formattedItems.length() - 2, formattedItems.length());

        return formattedItems.toString();
    }
}
