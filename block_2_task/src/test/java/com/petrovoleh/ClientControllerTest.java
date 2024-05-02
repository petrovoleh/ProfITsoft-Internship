package com.petrovoleh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrovoleh.model.Client;
import com.petrovoleh.service.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Test
    public void testGetClientByName() throws Exception {
        Client client = new Client("John", "john@example.com");

        when(clientService.getClientByName("John")).thenReturn(client);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/clients/John"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetAllClients() throws Exception {
        List<Client> clients = Arrays.asList(
                new Client("John", "john@example.com"),
                new Client("Alice", "alice@example.com")
        );

        when(clientService.getAllClients()).thenReturn(clients);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/clients"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(1)
    public void testCreateClient() throws Exception {
        Client client = new Client("John", "john@example.com");

        when(clientService.createClient(any(Client.class))).thenReturn(client);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/clients")
                        .content(asJsonString(client))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateClient() throws Exception {
        Client client = new Client("John", "john@example.com");

        when(clientService.updateClient(eq("John"), any(Client.class))).thenReturn(client);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/clients/John")
                        .content(asJsonString(client))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void testDeleteClient() throws Exception {
        when(clientService.deleteClient("John")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/clients/John"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    // Utility method to convert object to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
