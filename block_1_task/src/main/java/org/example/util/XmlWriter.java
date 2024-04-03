package org.example.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class XmlWriter {
    public static void writeStatisticsToXML(Map<String, Integer> orderStatistics, String attribute) {
        String fileName = "order_statistics_by_" + attribute + ".xml";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("<statistics>\n");
            for (Map.Entry<String, Integer> entry : orderStatistics.entrySet()) {
                writer.write("<item>\n");
                writer.write("<value>" + entry.getKey() + "</value>\n");
                writer.write("<count>" + entry.getValue() + "</count>\n");
                writer.write("</item>\n");
            }
            writer.write("</statistics>");
            System.out.println("Order statistics written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing order statistics to XML file: " + e.getMessage());
        }
    }
}
