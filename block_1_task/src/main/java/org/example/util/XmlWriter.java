
package org.example.util;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

    public class XmlWriter {

        @JsonPropertyOrder({ "value", "count" })
        static class Item {
            private String value;
            private int count;

            public Item(String value, int count) {
                this.value = value;
                this.count = count;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }

        @JsonRootName(value = "statistics")
        static class Statistics {
            @JacksonXmlElementWrapper(useWrapping = false)
            private List<Item> item;

            public Statistics(List<Item> item) {
                this.item = item;
            }

            public List<Item> getItem() {
                return item;
            }

            public void setItem(List<Item> item) {
                this.item = item;
            }


        }

        public static void writeStatisticsToXML(Map<String, Integer> orderStatistics, String attribute) {
            if ("".equals(attribute)) {
                System.err.println("Error writing order statistics to XML file: The attribute cannot be empty");
                return;
            }
            String fileName = "order_statistics_by_" + attribute + ".xml";
            File outputFile = new File(fileName);
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                List<Item> items = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : orderStatistics.entrySet()) {
                    items.add(new Item(entry.getKey(), entry.getValue()));
                }

                Statistics statistics = new Statistics(items);

                xmlMapper.writeValue(outputFile, statistics);
                System.out.println("Order statistics written to " + fileName);

            } catch (IOException e) {
                System.err.println("Error writing order statistics to XML file: " + e.getMessage());
            }
        }
    }
