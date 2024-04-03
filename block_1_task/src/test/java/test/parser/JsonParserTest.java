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
    void testParseClients() throws IOException {
        List<Client> clients = JsonParser.parseClients(objectMapper, clientsFile);
        assertNotNull(clients);
        assertEquals(2, clients.size());
    }

    @Test
    void testParseOrders() throws IOException {
        List<Order> orders = JsonParser.parseOrders(objectMapper, ordersFile);
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    void testParseClients_FileNotFound() {
        List<Client> clients = JsonParser.parseClients(objectMapper, "nonexistent_clients.json");
        assertNull(clients);
    }

    @Test
    void testParseOrders_FileNotFound() {
        List<Order> orders = JsonParser.parseOrders(objectMapper, "nonexistent_orders.json");
        assertNull(orders);
    }

    @Test
    void testParseClients_InvalidJson() {
        List<Client> clients = JsonParser.parseClients(objectMapper, ordersFile);
        assertNull(clients);
    }

    @Test
    void testParseOrders_InvalidJson() {
        List<Order> orders = JsonParser.parseOrders(objectMapper, clientsFile);
        assertNull(orders);
    }
}