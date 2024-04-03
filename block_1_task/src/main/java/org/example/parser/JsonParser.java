package org.example.parser;

import org.example.model.Client;
import org.example.model.Order;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JsonParser {

    private static File readFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("Error: File not found: " + fileName);
            return null;
        }
        return file;
    }

    private static <T> List<T> parseFile(ObjectMapper objectMapper, String fileName, Class<T[]> clazz) {
        File file = readFile(fileName);
        if (file == null) {
            return null;
        }
        try {
            return Arrays.asList(objectMapper.readValue(file, clazz));
        } catch (IOException e) {
            System.err.println("Error parsing file: " + fileName);
            e.printStackTrace();
            return null;
        }
    }

    public static List<Client> parseClients(ObjectMapper objectMapper, String fileName) {
        return parseFile(objectMapper, fileName, Client[].class);
    }

    public static List<Order> parseOrders(ObjectMapper objectMapper, String fileName) {
        return parseFile(objectMapper, fileName, Order[].class);
    }
}
