package test.parser;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Client;
import org.example.model.Order;
import org.example.parser.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String clientsFile = "./test_jsons/clients.json";
    private static final String ordersFile = "./test_jsons/orders.json";


    @Test
    void testParseOrders() throws IOException {
        List<Order> orders = JsonParser.parseOrders(objectMapper, ordersFile);
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }


    @Test
    void testParseOrders_FileNotFound() {
        List<Order> orders = JsonParser.parseOrders(objectMapper, "nonexistent_orders.json");
        assertNull(orders);
    }

    @Test
    void testParseOrders_InvalidJson() {
        List<Order> orders = JsonParser.parseOrders(objectMapper, clientsFile);
        assertNull(orders);
    }
}