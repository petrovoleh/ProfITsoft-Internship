package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Order;
import org.example.parser.JsonParser;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String emptyDirectory = "./test_jsons/empty_directory";
    private static final String invalidJsons = "./test_jsons/invalid_json";
    private static final String jsonDirectory = "./test_jsons";
    private static final String nonExistentFile = "nonexistent_orders.json";

    @Test
    void testParseOrders_ValidJson() {
        List<Order> orders = JsonParser.parseOrders(objectMapper, jsonDirectory);
        assertNotNull(orders);
        assertEquals(3, orders.size()); // Update expected size based on the provided JSON content
    }

    @Test
    void testParseOrders_FileNotFound() {
        List<Order> orders = JsonParser.parseOrders(objectMapper, nonExistentFile);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void testParseOrders_InvalidJson() {
        List<Order> orders = JsonParser.parseOrders(objectMapper, invalidJsons);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }
    @Test
    void testParseOrders_EmptyDirectory() {
        List<Order> orders = JsonParser.parseOrders(objectMapper, emptyDirectory);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }
}
