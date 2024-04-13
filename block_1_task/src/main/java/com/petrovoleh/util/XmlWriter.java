package com.petrovoleh.util;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.petrovoleh.model.Statistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Цей клас надає утилітарний метод для запису статистики замовлень у форматі XML.
 */
public class XmlWriter {

    /**
     * Записує статистику замовлень у форматі XML у вказаний файл.
     *
     * @param directoryPath    шлях до директорії для запису файлу
     * @param orderStatistics  мапа, що містить статистику замовлень (атрибут -> кількість)
     * @param attribute        атрибут, за яким обчислюється статистика
     * @throws IllegalArgumentException у разі, якщо атрибут порожній або містить лише пробіли
     * @throws RuntimeException         у разі помилки при записі у файл
     */
    public static void writeStatisticsToXML(String directoryPath, Map<String, Integer> orderStatistics, String attribute) {
        validateInputs(orderStatistics, attribute);

        String fileName = generateFileName(directoryPath, attribute);
        createDirectoryIfNotExists(directoryPath);
        writeStatisticsToFile(orderStatistics, fileName);
    }

    /**
     * Перевіряє вхідні дані на валідність.
     *
     * @param orderStatistics мапа, що містить статистику замовлень
     * @param attribute       атрибут, за яким обчислюється статистика
     * @throws IllegalArgumentException у разі невалідності вхідних даних
     */
    private static void validateInputs(Map<String, Integer> orderStatistics, String attribute) {
        if (attribute == null || attribute.trim().isEmpty()) {
            throw new IllegalArgumentException("Error writing order statistics to XML file: The attribute cannot be empty");
        }
        if (orderStatistics == null || orderStatistics.isEmpty()) {
            throw new IllegalArgumentException("Error writing order statistics to XML file: The orderStatistics cannot be empty");
        }
    }

    /**
     * Генерує ім'я файлу для запису статистики.
     *
     * @param directoryPath шлях до директорії для запису файлу
     * @param attribute     атрибут, за яким обчислюється статистика
     * @return ім'я файлу
     */
    private static String generateFileName(String directoryPath, String attribute) {
        return directoryPath + "/" + "order_statistics_by_" + attribute.trim() + ".xml";
    }

    /**
     * Створює директорію, якщо вона не існує.
     *
     * @param directoryPath шлях до директорії для перевірки та створення
     */
    private static void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Записує статистику у файл у форматі XML.
     *
     * @param orderStatistics мапа, що містить статистику замовлень
     * @param fileName        ім'я файлу для запису
     * @throws RuntimeException у разі помилки при записі у файл
     */
    private static void writeStatisticsToFile(Map<String, Integer> orderStatistics, String fileName) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

            List<Map<String, Object>> items = prepareDataForXML(orderStatistics);

            File outputFile = new File(fileName);
            xmlMapper.writeValue(outputFile, new Statistics(items));
            System.out.println("Order statistics written to " + fileName);
        } catch (IOException e) {
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
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : orderStatistics.entrySet()) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("value", entry.getKey());
            itemMap.put("count", entry.getValue());
            items.add(itemMap);
        }
        return items;
    }
}
