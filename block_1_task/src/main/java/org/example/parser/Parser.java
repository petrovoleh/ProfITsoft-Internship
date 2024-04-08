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

/**
 * Цей клас забезпечує функціональність парсингу JSON-файлів та обчислення статистики замовлень.
 */
public class Parser {

    private static final int STATISTIC_INTERVAL = 1000; // Інтервал оновлення статистики

    /**
     * Парсує JSON-файл та повертає список замовлень.
     *
     * @param jsonFactory фабрика JsonFactory
     * @param file        файл для парсингу
     * @param attribute   атрибут, за яким обчислюється статистика
     * @return список замовлень
     */
    public static List<Order> parseFile(JsonFactory jsonFactory, File file, String attribute) {
        // Ініціалізуємо пустий список замовлень
        List<Order> orders = new ArrayList<>();
        // Лічильник оброблених замовлень
        int orderCount = 0;
        try {
            // Створюємо парсер JSON
            JsonParser jsonParser = jsonFactory.createParser(file);
            // Поки не дійшли до кінця масиву JSON
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                // Якщо поточний токен - початок об'єкта
                if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    // Читаємо замовлення з JSON та додаємо його до списку
                    Order order = readOrder(jsonParser);
                    if (order != null) {
                        orders.add(order);
                        orderCount++;
                    }
                    // Перевірка, чи настав час оновлення статистики
                    if (orderCount % STATISTIC_INTERVAL == 0) {
                        updateStatistics(orders, attribute);
                        // Очищаємо список замовлень для наступного інтервалу
                        orders.clear();
                    }
                }
            }
            // Закриваємо парсер JSON
            jsonParser.close();
            // Якщо список замовлень не порожній, оновлюємо статистику
            if (!orders.isEmpty()) {
                updateStatistics(orders, attribute);
            } else {
                // Якщо список порожній, виводимо повідомлення про помилку
                System.err.println("Error parsing file: " + file);
                System.err.println("JSON file does not contain any order");
            }
        } catch (IOException e) {
            // Логуємо помилку парсингу
            logError(file, e);
        }
        // Повертаємо список замовлень
        return orders;
    }

    /**
     * Оновлює статистику замовлень.
     *
     * @param orders    список замовлень
     * @param attribute атрибут, за яким обчислюється статистика
     */
    private static void updateStatistics(List<Order> orders, String attribute) {
        // Викликаємо сервіс для обчислення статистики
        StatsService.calculateOrderStatistics(orders, attribute);
    }

    /**
     * Читає об'єкт замовлення з JSON.
     *
     * @param jsonParser парсер JSON
     * @return об'єкт замовлення
     * @throws IOException у випадку помилки читання JSON
     */
    private static Order readOrder(JsonParser jsonParser) throws IOException {
        // Ініціалізуємо порожнє замовлення
        Order order = new Order();
        // Поки не дійшли до кінця об'єкта JSON
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            // Отримуємо назву поля
            String fieldName = jsonParser.getCurrentName();
            // Переходимо до значення поля
            jsonParser.nextToken();
            // Обробляємо поля замовлення
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
                    // Якщо зустрічено неочікуване поле, виводимо помилку
                    System.err.println("Error: Element contain unexpected field: \"" + fieldName + "\"");
                    return null;
            }
        }
        // Повертаємо замовлення
        return order;
    }

    /**
     * Читає список товарів з JSON.
     *
     * @param jsonParser парсер JSON
     * @return список товарів
     * @throws IOException у випадку помилки читання JSON
     */
    private static List<String> readItems(JsonParser jsonParser) throws IOException {
        // Ініціалізуємо порожній список товарів
        List<String> items = new ArrayList<>();
        // Поки не дійшли до кінця масиву JSON
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            // Додаємо товар до списку
            items.add(jsonParser.getText());
        }
        // Повертаємо список товарів
        return items;
    }

    /**
     * Парсить всі JSON-файли у вказаній директорії та повертає список замовлень.
     *
     * @param jsonFactory    фабрика JsonFactory
     * @param directoryPath  шлях до директорії
     * @param attribute      атрибут, за яким обчислюється статистика
     * @return список замовлень
     */
    public static List<Order> parseOrders(JsonFactory jsonFactory, String directoryPath, String attribute) {
        // Ініціалізуємо порожній список замовлень
        List<Order> orders = new ArrayList<>();
        // Отримуємо список файлів у директорії
        List<File> files = getAllFilesInDirectory(directoryPath);
        // Якщо список файлів не порожній
        if (!files.isEmpty()) {
            // Парсимо кожен файл і додаємо замовлення до загального списку
            for (File file : files) {
                List<Order> parsedOrders = parseFile(jsonFactory, file, attribute);
                orders.addAll(parsedOrders);
            }
        } else {
            // Якщо список файлів порожній, виводимо повідомлення про помилку
            System.err.println("No JSON files found in directory or directory does not exist: " + directoryPath);
        }
        // Повертаємо список замовлень
        return orders;
    }

    /**
     * Отримує список всіх файлів у вказаній директорії.
     *
     * @param directoryPath шлях до директорії
     * @return список файлів у директорії
     */
    private static List<File> getAllFilesInDirectory(String directoryPath) {
        // Ініціалізуємо порожній список файлів
        List<File> files = new ArrayList<>();
        // Створюємо об'єкт директорії
        File directory = new File(directoryPath);
        // Якщо директорія існує
        if (directory.isDirectory()) {
            // Додаємо всі файли у директорії до списку
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isFile()) {
                    files.add(file);
                }
            }
        }
        // Повертаємо список файлів
        return files;
    }

    /**
     * Логує помилку парсингу файлу.
     *
     * @param file файл, який було спробовано розпарсити
     * @param e    виняток, який виник під час парсингу
     */
    private static void logError(File file, IOException e) {
        // Виводимо повідомлення про помилку разом з шляхом файлу та повідомленням винятку
        System.err.println("Error parsing file: " + file.getAbsolutePath());
        System.err.println("Exception message: " + e.getMessage());
    }
}
