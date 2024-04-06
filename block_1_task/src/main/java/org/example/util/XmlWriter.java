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
     * Записує статистику замовлень у форматі XML.
     * @param orderStatistics статистика замовлень
     * @param attribute атрибут, за яким обчислена статистика
     */
    public static void writeStatisticsToXML(Map<String, Integer> orderStatistics, String attribute) {
        if (attribute == null || attribute.trim().isEmpty()) {
            throw new IllegalArgumentException("Error writing order statistics to XML file: The attribute cannot be empty");
        }

        String fileName = "order_statistics_by_" + attribute.trim() + ".xml";

        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

            List<Map<String, Object>> items = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : orderStatistics.entrySet()) {
                // Створення мапи для кожного елемента
                Map<String, Object> itemMap = Map.of(
                        "value", entry.getKey(),
                        "count", entry.getValue()
                );
                items.add(itemMap);
            }

            File outputFile = new File(fileName);
            xmlMapper.writeValue(outputFile, new Statistics(items));
            System.out.println("Order statistics written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing order statistics to XML file: " + e.getMessage());
            throw new RuntimeException("Error writing order statistics to XML file", e);
        }
    }
}
