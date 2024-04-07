package org.example;

import com.fasterxml.jackson.core.JsonFactory;
import org.example.model.Order;
import org.example.parser.Parser;
import org.example.service.StatsService;
import org.example.util.XmlWriter;

import java.util.List;
import java.util.Map;

public class Main {
    private static final JsonFactory jsonFactory = new JsonFactory();
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java -jar Main.java <directory_path> <attribute>");
            return;
        }

        String directoryPath = args[0];
        String attribute = args[1];


        // Parse all orders from JSON files in the directory
        List<Order> orders = Parser.parseOrders(jsonFactory, directoryPath,attribute);

        if (orders.isEmpty()) {
            System.err.println("No orders found in the directory.");
        } else {
            // Calculate order statistics
            System.out.println("Calculating order statistics...");
            Map<String, Integer> orderStatistics = StatsService.getStatistics();
            XmlWriter.writeStatisticsToXML(orderStatistics, attribute);
        }
    }
}