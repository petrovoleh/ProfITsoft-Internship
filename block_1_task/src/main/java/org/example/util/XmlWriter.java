package org.example.util;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.example.model.Statistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Цей клас надає утилітарний метод для запису статистики замовлень у форматі XML.
 */
public class XmlWriter {

    /**
     * Записує статистику замовлень у форматі XML у вказаний файл.
     *
     * @param orderStatistics мапа, що містить статистику замовлень (атрибут -> кількість)
     * @param attribute       атрибут, за яким обчислюється статистика
     * @throws IllegalArgumentException у разі, якщо атрибут порожній або містить лише пробіли
     * @throws RuntimeException         у разі помилки при записі у файл
     */
    public static void writeStatisticsToXML(Map<String, Integer> orderStatistics, String attribute) {
        // Перевірка на валідність атрибуту
        if (attribute == null || attribute.trim().isEmpty()) {
            throw new IllegalArgumentException("Error writing order statistics to XML file: The attribute cannot be empty");
        }

        // Формування імені файлу для запису
        String fileName = "order_statistics_by_" + attribute.trim() + ".xml";

        try {
            // Ініціалізація XML мапера
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Підготовка даних для запису у файл
            List<Map<String, Object>> items = prepareDataForXML(orderStatistics);

            // Запис даних у файл
            File outputFile = new File(fileName);
            xmlMapper.writeValue(outputFile, new Statistics(items));
            System.out.println("Order statistics written to " + fileName);
        } catch (IOException e) {
            // Обробка помилки при записі у файл
            System.err.println("Error writing order statistics to XML file: " + e.getMessage());
            throw new RuntimeException("Error writing order statistics to XML file", e);
        }
    }

    /**
     * Підготовлює дані для запису у файл у форматі XML.
     *
     * @param orderStatistics мапа, що містить статистику замовлень (атрибут -> кількість)
     * @return список мап, що містить дані для запису у файл
     */
    private static List<Map<String, Object>> prepareDataForXML(Map<String, Integer> orderStatistics) {
        // Ініціалізація списку для підготовлених даних
        List<Map<String, Object>> items = new ArrayList<>();
        // Обробка кожного запису статистики
        for (Map.Entry<String, Integer> entry : orderStatistics.entrySet()) {
            // Створення мапи для кожного елемента
            Map<String, Object> itemMap = Map.of(
                    "value", entry.getKey(),
                    "count", entry.getValue()
            );
            items.add(itemMap);
        }
        return items;
    }
}
