package org.example.service;

import org.example.model.Order;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Цей клас надає сервіс для обчислення статистики замовлень.
 */
public class StatsService {

    /**
     * Обчислює статистику замовлень згідно з вказаним атрибутом.
     * @param orders список замовлень
     * @param attribute атрибут, за яким обчислюється статистика ("item", "orderId", "orderDate", "amount", "client")
     * @return мапа, що містить статистику
     */
    public static Map<String, Integer> calculateOrderStatistics(List<Order> orders, String attribute) {
        Map<String, Integer> statistics = new HashMap<>();
        for (Order order : orders) {
            List<String> items = order.getItems();
            if (attribute.equals("item")) {
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

    /**
     * Виводить повідомлення про неправильний атрибут і припиняє виконання програми.
     * @param attribute неправильний атрибут
     */
    private static void incorrectValue(String attribute) {
        System.err.println("Error: Invalid attribute value: " + attribute);
        System.err.println("Valid attributes: orderId, orderDate, amount, client, item");
        System.exit(1);
    }

    /**
     * Повертає значення вказаного атрибута для замовлення.
     * @param order замовлення
     * @param attribute атрибут
     * @return значення атрибута
     */
    private static String getValueForAttribute(Order order, String attribute) {
        return switch (attribute) {
            case "orderId" -> String.valueOf(order.getOrderId());
            case "orderDate" -> String.valueOf(order.getOrderDate());
            case "amount" -> String.valueOf(order.getAmount());
            case "client" -> order.getClient();
            default -> {
                incorrectValue(attribute);
                yield null;
            }
        };
    }
}
