package com.petrovoleh.service;

import com.petrovoleh.model.Order;
import com.petrovoleh.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getOrderById(int id) {
        return orderRepository.getOrderById(id);
    }

    public Order createOrder(Order order) {
        // Check if order with the same id already exists
        Order existingOrder = orderRepository.getOrderById(order.getOrderId());
        if (existingOrder != null) {
            return null; // Order with the same id already exists
        }
        return orderRepository.createOrder(order);
    }

    public Order updateOrder(int id, Order updatedOrder) {
        Order existingOrder = orderRepository.getOrderById(id);
        if (existingOrder != null) {
            updatedOrder.setOrderId(id);
            return orderRepository.updateOrder(updatedOrder);
        }
        return null; // Order not found
    }

    public boolean deleteOrder(int id) {
        Order existingOrder = orderRepository.getOrderById(id);
        if (existingOrder != null) {
            orderRepository.deleteOrder(id);
            return true;
        }
        return false; // Order not found
    }

    public int saveOrders(List<Order> orders) {
        int successfulImports = 0;
        for (Order order : orders) {
            try {
                orderRepository.createOrder(order);
                successfulImports++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return successfulImports;
    }
    public Page<Order> getOrdersByName(String name, int page, int size) {
        return orderRepository.findByClient(name, page, size);
    }
    public List<Order> getAllOrdersByName(String name) {
        return orderRepository.findAllByClient(name);
    }


    public String generateCsvContent(List<Order> orders) {
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
