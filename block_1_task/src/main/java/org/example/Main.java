package org.example;

import com.fasterxml.jackson.core.JsonFactory;
import org.example.model.Order;
import org.example.parser.Parser;
import org.example.service.StatsService;
import org.example.util.XmlWriter;

import java.util.List;
import java.util.Map;

/**
 * Головний клас програми.
 */
public class Main {
    private static final JsonFactory jsonFactory = new JsonFactory();

    /**
     * Головний метод програми.
     *
     * @param args аргументи командного рядка
     *             args[0] - шлях до папки
     *             args[1] - атрибут по якому робити статистику
     */
    public static void main(String[] args) {
        // Перевірка наявності необхідної кількості аргументів
        if (args.length < 2) {
            System.out.println("Usage: java -jar Main.java <directory_path> <attribute>");
            return;
        }
        // Отримання шляху до директорії та атрибуту з аргументів командного рядка
        String directoryPath = args[0];
        String attribute = args[1];

        // Розбір всіх замовлень з JSON-файлів у директорії
        System.out.println("Parsing files and calculating order statistics...");
        List<Order> orders = Parser.parseOrders(jsonFactory, directoryPath,attribute);

        if (orders.isEmpty()) {
            System.err.println("No orders found in the directory.");
        } else {
            System.out.println("Writing statistics to file...");
            Map<String, Integer> orderStatistics = StatsService.getStatistics();
            XmlWriter.writeStatisticsToXML(orderStatistics, attribute);
        }
    }
}