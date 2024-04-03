package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Order;
import org.example.parser.JsonParser;
import org.example.service.StatsService;
import org.example.util.XmlWriter;

import java.util.List;
import java.util.Map;

public class Main {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java -jar Main.java <directory_path> <attribute>");
            return;
        }

        String directoryPath = args[0];
        String attribute = args[1];


        // Parse all orders from JSON files in the directory
        List<Order> orders = JsonParser.parseOrders(objectMapper, directoryPath);

        if (orders.isEmpty()) {
            System.out.println("No orders found in the directory.");
        } else {
            // Calculate order statistics
            System.out.println("Calculating order statistics...");
            Map<String, Integer> orderStatistics = StatsService.calculateOrderStatistics(orders, attribute);
            XmlWriter.writeStatisticsToXML(orderStatistics, attribute);
        }
    }
}