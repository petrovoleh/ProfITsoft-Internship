package test.parser;

import com.fasterxml.jackson.core.JsonFactory;
import org.example.model.Order;
import org.example.parser.Parser;
import org.example.service.StatsService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        Parser.parseOrders(jsonFactory, jsonDirectory, attribute);
        Map<String, Integer> stats = StatsService.getStatistics();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
        StatsService.clearStatistics();
    }

    /**
     * Тест для перевірки поведінки, коли файл не знайдено.
     */
    @Test
    void testParseOrders_FileNotFound() {
        Parser.parseOrders(jsonFactory, nonExistentFile, attribute);
        Map<String, Integer> stats = StatsService.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    /**
     * Тест для перевірки поведінки при парсингу невалідного JSON-файлу.
     */
    @Test
    void testParseOrders_InvalidJson() {
        Parser.parseOrders(jsonFactory, invalidJsons, attribute);
        Map<String, Integer> stats = StatsService.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    /**
     * Тест для перевірки парсингу порожньої директорії.
     */
    @Test
    void testParseOrders_EmptyDirectory() {
        Parser.parseOrders(jsonFactory, emptyDirectory, attribute);
        Map<String, Integer> stats = StatsService.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    /**
     * Тест для перевірки поведінки, коли директорія не існує.
     */
    @Test
    void testParseOrders_DirectoryDoesNotExist() {
        Parser.parseOrders(jsonFactory, noDirectory, attribute);
        Map<String, Integer> stats = StatsService.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.isEmpty());
        StatsService.clearStatistics();
    }


    /**
     * Тест для перевірки парсингу багатьох JSON-файлів.
     */
    @Test
    void testParseOrders_ManyFiles() {
        Parser.parseOrders(jsonFactory, manyFilesDirectory, attribute);
        Map<String, Integer> stats = StatsService.getStatistics();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
        StatsService.clearStatistics();
    }
    /**
     * Тест для перевірки парсингу великого JSON-файлу.
     */
    @Test
    void testParseOrders_BigFile() {
        Parser.parseOrders(jsonFactory, bigFileDirectory, attribute);
        Map<String, Integer> stats = StatsService.getStatistics();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
        StatsService.clearStatistics();
    }
}
