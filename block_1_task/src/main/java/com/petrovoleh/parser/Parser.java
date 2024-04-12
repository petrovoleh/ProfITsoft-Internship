package com.petrovoleh.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.petrovoleh.service.StatsService;
import com.petrovoleh.model.Order;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Клас Parser відповідає за парсинг JSON-файлів, обробку замовлень та виклик сервісу статистики.
 */
public class Parser {

    // Інтервал для обчислення статистики (кількість замовлень, після якої статистика обчислюється)
    private static final int STATISTIC_INTERVAL = 500;

    /**
     * Метод для парсингу одного JSON-файлу.
     *
     * @param jsonFactory фабрика JSON
     * @param file        файл для парсингу
     * @param attribute   атрибут для обчислення статистики
     * @throws IOException у випадку помилки вводу/виводу
     */
    public static void parseFile(JsonFactory jsonFactory, File file, String attribute) throws IOException {
        List<Order> orders = new ArrayList<>();
        JsonParser jsonParser = jsonFactory.createParser(file);
        boolean hasOrders = false;
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                Order order = readOrder(jsonParser);
                if (order != null) {
                    orders.add(order);
                    // Обчислення статистики, якщо досягнуто інтервал
                    if (orders.size() % STATISTIC_INTERVAL == 0) {
                        hasOrders = true;
                        StatsService.calculateOrderStatistics(orders, attribute);
                        orders.clear();
                    }
                }
            }
        }

        jsonParser.close();

        // Обчислення статистики для залишкових замовлень, якщо такі є
        if (!orders.isEmpty()) {
            hasOrders = true;
            StatsService.calculateOrderStatistics(orders, attribute);
            orders.clear();
        }
        if (!hasOrders) {
            logError(file, new IOException("JSON file does not contain any order"));
        }
    }

    /**
     * Метод для парсингу замовлення з JSON.
     *
     * @param jsonParser парсер JSON
     * @return замовлення
     * @throws IOException у випадку помилки вводу/виводу
     */
    private static Order readOrder(JsonParser jsonParser) throws IOException {
        Order order = new Order();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();
            switch (fieldName) {
                case "orderId":
                    order.setOrderId(jsonParser.getIntValue());
                    break;
                case "orderDate":
                    order.setOrderDate(new Date(jsonParser.getLongValue()));
                    break;
                case "client":
                    order.setClient(jsonParser.getText());
                    break;
                case "amount":
                    order.setAmount(jsonParser.getIntValue());
                    break;
                case "items":
                    List<String> items = readItems(jsonParser);
                    order.setItems(items);
                    break;
                default:
                    logError(null, new IOException("Element contains unexpected field: \"" + fieldName + "\""));
                    return null;
            }
        }
        return order;
    }

    /**
     * Метод для парсингу списку предметів з JSON.
     *
     * @param jsonParser парсер JSON
     * @return список предметів
     * @throws IOException у випадку помилки вводу/виводу
     */
    private static List<String> readItems(JsonParser jsonParser) throws IOException {
        List<String> items = new ArrayList<>();
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            items.add(jsonParser.getText());
        }
        return items;
    }

    /**
     * Метод для парсингу замовлень з JSON-файлів у директорії.
     *
     * @param jsonFactory   фабрика JSON
     * @param directoryPath шлях до директорії
     * @param attribute     атрибут для обчислення статистики
     */
    public static void parseOrders(JsonFactory jsonFactory, String directoryPath, String attribute) {
        parseOrdersThreads(jsonFactory, directoryPath, attribute, 4);
    }

    /**
     * Метод для парсингу замовлень з JSON-файлів у директорії з використанням потоків.
     *
     * @param jsonFactory   фабрика JSON
     * @param directoryPath шлях до директорії
     * @param attribute     атрибут для обчислення статистики
     * @param threadCount   кількість потоків
     */
    public static void parseOrdersThreads(JsonFactory jsonFactory, String directoryPath, String attribute, int threadCount) {
        List<File> files = getAllFilesInDirectory(directoryPath);
        if (files.isEmpty()) {
            logError(null, new IOException("No JSON files found in directory or directory does not exist: " + directoryPath));
            return;
        }
        int numThreads = Math.min(threadCount, files.size()); // Забезпечення, щоб кількість потоків не перевищувала кількість файлів
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        int filesPerThread = files.size() / numThreads;
        int startIndex = 0;
        int endIndex = filesPerThread;
        for (int i = 0; i < numThreads; i++) {
            if (i == numThreads - 1) {
                endIndex = files.size(); // Забезпечення, що останній потік бере залишкові файли
            }
            List<File> filesSubset = files.subList(startIndex, endIndex);
            startIndex = endIndex;
            endIndex = Math.min(endIndex + filesPerThread, files.size());
            executor.submit(() -> {
                for (File file : filesSubset) {
                    try {
                        parseFile(jsonFactory, file, attribute); // Кожен потік парсить свій власний піднабір файлів
                    } catch (IOException e) {
                        logError(file, e);
                    }
                }
            });
        }
        executor.shutdown();
        try {
            // Очікування завершення всіх завдань або до досягнення встановленого таймауту
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                // Якщо не всі завдання завершилися протягом таймауту, вивести повідомлення про помилку
                logError(null, new RuntimeException("Not all tasks completed within the specified timeout."));
            }
        } catch (InterruptedException e) {
            // Обробка InterruptedException, якщо він виникає під час очікування завершення
            logError(null, e);
        }
    }

    /**
     * Метод для отримання всіх файлів у директорії.
     *
     * @param directoryPath шлях до директорії
     * @return список файлів у директорії
     */
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

    /**
     * Метод для логування помилок.
     *
     * @param file файл, в якому виникла помилка (може бути null)
     * @param e    виняток
     */
    private static void logError(File file, Exception e) {
        System.err.println("Error: " + e.getMessage() + (file != null ? " in file: " + file.getAbsolutePath() : ""));
    }
}
