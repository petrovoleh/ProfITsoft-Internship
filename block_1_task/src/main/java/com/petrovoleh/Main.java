package com.petrovoleh;

import com.fasterxml.jackson.core.JsonFactory;
import com.petrovoleh.parser.Parser;
import com.petrovoleh.service.StatsService;
import com.petrovoleh.util.XmlWriter;

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
        Parser.parseOrders(jsonFactory, directoryPath,attribute, 10);
        Map<String, Integer> orderStatistics = StatsService.getStatistics();
        if (orderStatistics.isEmpty()) {
            System.err.println("No orders found in the directory.");

        } else {
            System.out.println("Writing statistics to file...");
            XmlWriter.writeStatisticsToXML(orderStatistics, attribute);
        }
    }
}