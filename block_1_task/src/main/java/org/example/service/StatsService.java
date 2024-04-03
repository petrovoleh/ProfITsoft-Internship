package org.example.service;

import org.example.model.Order;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsService {


    public static Map<String, Integer> calculateOrderStatistics(List<Order> orders, String attribute) {
        Map<String, Integer> statistics = new HashMap<>();
        for (Order order : orders) {
            List<String> items = order.getItems();
            if (attribute.equals("items")) {
                for (String item : items) {
                    statistics.put(item, statistics.getOrDefault(item, 0) + 1);
                }
            } else {
                String value = getValueForAttribute(order, attribute);
                statistics.put(value, statistics.getOrDefault(value, 0) + 1);
            }
        }
        return statistics;
    }


    private static String getValueForAttribute(Order order, String attribute) {
        return switch (attribute) {
            case "orderId" -> String.valueOf(order.getOrderId());
            case "orderDate" -> String.valueOf(order.getOrderDate());
            case "amount" -> String.valueOf(order.getAmount());
            case "client" -> order.getClient();
            // Handle other order attributes if needed
            default -> null;
        };
    }
}
