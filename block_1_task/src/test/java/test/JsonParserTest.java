package test;

import com.fasterxml.jackson.core.JsonFactory;
import org.example.model.Order;
import org.example.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Клас для тестування парсера JSON-файлів.
 */
public class JsonParserTest {

    private static final JsonFactory jsonFactory = new JsonFactory();
    private static final String emptyDirectory = "./test_jsons/empty_directory";
    private static final String noDirectory = "./test_jsons/nodir";
    private static final String invalidJsons = "./test_jsons/invalid_json";
    private static final String jsonDirectory = "./test_jsons";
    private static final String bigFileDirectory = "./test_jsons/big_file";
    private static final String manyFilesDirectory = "./test_jsons/many_files";
    private static final String nonExistentFile = "nonexistent_orders.json";
    private static final String attribute = "item";

    /**
     * Тест для перевірки парсингу валідного JSON-файлу.
     */
    @Test
    void testParseOrders_ValidJson() {
        List<Order> orders = Parser.parseOrders(jsonFactory, jsonDirectory, attribute);
        assertNotNull(orders);
        assertEquals(3, orders.size()); // Оновіть очікуваний розмір на основі наданого вмісту JSON
    }

    /**
     * Тест для перевірки поведінки, коли файл не знайдено.
     */
    @Test
    void testParseOrders_FileNotFound() {
        List<Order> orders = Parser.parseOrders(jsonFactory, nonExistentFile, attribute);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    /**
     * Тест для перевірки поведінки при парсингу невалідного JSON-файлу.
     */
    @Test
    void testParseOrders_InvalidJson() {
        List<Order> orders = Parser.parseOrders(jsonFactory, invalidJsons, attribute);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    /**
     * Тест для перевірки парсингу порожньої директорії.
     */
    @Test
    void testParseOrders_EmptyDirectory() {
        List<Order> orders = Parser.parseOrders(jsonFactory, emptyDirectory, attribute);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    /**
     * Тест для перевірки поведінки, коли директорія не існує.
     */
    @Test
    void testParseOrders_DirectoryDoesNotExist() {
        List<Order> orders = Parser.parseOrders(jsonFactory, noDirectory, attribute);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    /**
     * Тест для перевірки парсингу багатьох JSON-файлів.
     */
    @Test
    void testParseOrders_ManyFiles() {
        List<Order> orders = Parser.parseOrders(jsonFactory, manyFilesDirectory, attribute);
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }
    /**
     * Тест для перевірки парсингу великого JSON-файлу.
     */
    @Test
    void testParseOrders_BigFile() {
        List<Order> orders = Parser.parseOrders(jsonFactory, bigFileDirectory, attribute);
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }
}
