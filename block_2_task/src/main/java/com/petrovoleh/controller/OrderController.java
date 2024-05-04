
package com.petrovoleh.controller;

import com.petrovoleh.model.Client;
import com.petrovoleh.model.Order;
import com.petrovoleh.model.OrderResponse;
import com.petrovoleh.model.OrderListRequest;
import com.petrovoleh.model.OrderListResponse;
import com.petrovoleh.service.ClientService;
import com.petrovoleh.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Клас OrderController обробляє HTTP-запити, пов'язані з операціями замовлення.
 */
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

    /**
     * Отримує замовлення за ідентифікатором.
     *
     * @param id Ідентифікатор замовлення.
     * @return ResponseEntity із замовленням та HTTP-статусом OK у разі успіху, або HTTP-статусом NOT_FOUND, якщо замовлення не знайдене.
     */
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

    /**
     * Створює нове замовлення.
     *
     * @param order Об'єкт замовлення для створення.
     * @return ResponseEntity із створеним замовленням та HTTP-статусом CREATED у разі успіху, або HTTP-статусом CONFLICT, якщо замовлення вже існує.
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = service.createOrder(order);
        if (createdOrder != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Оновлює існуюче замовлення.
     *
     * @param id          Ідентифікатор замовлення для оновлення.
     * @param updatedOrder Оновлений об'єкт замовлення.
     * @return ResponseEntity із оновленим замовленням та HTTP-статусом OK у разі успіху, або HTTP-статусом NOT_FOUND, якщо замовлення не знайдене.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable int id, @RequestBody Order updatedOrder) {
        Order order = service.updateOrder(id, updatedOrder);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Видаляє замовлення за ідентифікатором.
     *
     * @param id Ідентифікатор замовлення для видалення.
     * @return ResponseEntity з HTTP-статусом NO_CONTENT у разі успіху, або HTTP-статусом NOT_FOUND, якщо замовлення не знайдене.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) {
        boolean deleted = service.deleteOrder(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Завантажує замовлення.
     *
     * @param orders Список замовлень для завантаження.
     * @return ResponseEntity із відповіддю та HTTP-статусом OK.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadOrders(@RequestBody List<Order> orders) {
        // Парсити JSON-файл і передавати список замовлень на обробку сервісу
        int successfulImports = service.saveOrders(orders);
        int failedImports = orders.size() - successfulImports;
        String response = "{\"successfulImports\": " + successfulImports + ", \"failedImports\": " + failedImports + "}";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Генерує звіт.
     *
     * @param request Об'єкт запиту на звіт.
     * @return ResponseEntity із звітом та HTTP-статусом OK.
     */
    @PostMapping("/_report")
    public ResponseEntity<byte[]> generateReport(@RequestBody OrderListRequest request) {
        List<Order> allOrders = service.getAllOrdersByName(request.getName());
        // Generate CSV content
        String csvContent = service.generateCsvContent(allOrders);

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

    /**
     * Постійно змінює список.
     *
     * @param request Об'єкт запиту на список.
     * @return ResponseEntity із списком та HTTP-статусом OK.
     */
    @PostMapping("/_list")
    public ResponseEntity<OrderListResponse> postList(@RequestBody OrderListRequest request) {
        Page<Order> ordersPage = service.getOrdersByName(request.getName(), request.getPage(), request.getSize());

        List<Order> orders = ordersPage.getContent(); // Get list of orders for the current page
        int totalPages = ordersPage.getTotalPages(); // Get total number of pages

        OrderListResponse response = new OrderListResponse(orders, totalPages);
        return ResponseEntity.ok(response);
    }
}
