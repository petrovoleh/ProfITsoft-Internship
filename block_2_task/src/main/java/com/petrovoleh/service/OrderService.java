package com.petrovoleh.service;

import com.petrovoleh.model.Order;
import com.petrovoleh.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
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
    public List<Order> getOrdersByName(String name, int page, int size) {
        Page<com.petrovoleh.model.Order> ordersPage = orderRepository.findByClient(name, page, size);
        return ordersPage.getContent();
    }
}
