package org.example.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.example.model.Order;
import org.example.service.StatsService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Parser {

    private static final int STATISTIC_INTERVAL = 1000; // Interval to update statistics

    public static List<Order> parseFile(JsonFactory jsonFactory, File file, String attribute) {
        List<Order> orders = new ArrayList<>();
        int orderCount = 0; // Counter to keep track of orders processed
        try {
            JsonParser jsonParser = jsonFactory.createParser(file);
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    orders.add(readOrder(jsonParser));
                    orderCount++;
                    // Check if it's time to update statistics
                    if (orderCount % STATISTIC_INTERVAL == 0) {
                        StatsService.calculateOrderStatistics(orders,attribute);
                        orders = new ArrayList<>();
                    }
                }
            }
            jsonParser.close();
            StatsService.calculateOrderStatistics(orders,attribute);
        } catch (IOException e) {
            // Log the error and continue processing other files
            System.err.println("Error parsing file: " + file);
            e.printStackTrace();
        }
        return orders;
    }

    private static Order readOrder(JsonParser jsonParser) throws IOException {
        Order order = new Order();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken(); // Move to the value token
            switch (fieldName) {
                case "orderId":
                    order.setOrderId(jsonParser.getIntValue());
                    break;
                case "orderDate":
                    // Assuming the date is in milliseconds
                    order.setOrderDate(new Date(jsonParser.getLongValue()));
                    break;
                case "client":
                    order.setClient(jsonParser.getText());
                    break;
                case "amount":
                    order.setAmount(jsonParser.getIntValue());
                    break;
                case "items":
                    List<String> items = new ArrayList<>();
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        items.add(jsonParser.getText());
                    }
                    order.setItems(items);
                    break;
                default:
                    // Handle unrecognized fields or ignore them
                    break;
            }
        }
        return order;
    }

    public static List<Order> parseOrders(JsonFactory jsonFactory, String directoryPath, String attribute) {
        List<Order> orders = new ArrayList<>();
        List<File> files = getAllFilesInDirectory(directoryPath);
        if (!files.isEmpty()) {
            for (File file : files) {
                List<Order> parsedOrders = parseFile(jsonFactory, file, attribute);
                if (parsedOrders != null) {
                    orders.addAll(parsedOrders);
                }
            }
        } else {
            System.err.println("No JSON files found in directory: " + directoryPath);
        }
        return orders;
    }

    private static List<File> getAllFilesInDirectory(String directoryPath) {
        List<File> files = new ArrayList<>();
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isFile()) {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
