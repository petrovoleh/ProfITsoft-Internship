package org.example.parser;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.example.model.Order;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JsonParser {

    private static List<File> getAllFilesInDirectory(String directoryPath) {
        List<File> files = new ArrayList<>();
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            Collections.addAll(files, directory.listFiles());
        }
        return files;
    }


    private static List<Order> parseFile(ObjectMapper objectMapper, File file) {
        try {

            return Arrays.asList(objectMapper.readValue(file, Order[].class));
        } catch (UnrecognizedPropertyException e) {
            // Log the error and continue processing other files
            System.err.println("Error parsing file: " + file + ". Unrecognized property: " + e.getPropertyName());
            return new ArrayList<>();
        } catch (IOException e) {
            // Log the error and continue processing other files
            System.err.println("Error parsing file: " + file);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static List<Order> parseOrders(ObjectMapper objectMapper, String directoryPath) {
        List<Order> orders = new ArrayList<>();
        List<File> files = getAllFilesInDirectory(directoryPath);
        if (!files.isEmpty()) {
            for (File file : files) {
                List<Order> parsedOrders = parseFile(objectMapper, file);
                if (parsedOrders != null) {
                    orders.addAll(parsedOrders);
                }
            }
        } else {
            System.err.println("No JSON files found in directory: " + directoryPath);
        }
        return orders;
    }
}
