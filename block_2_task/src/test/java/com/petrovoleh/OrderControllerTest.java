package com.petrovoleh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrovoleh.model.Order;
import com.petrovoleh.model.OrderListRequest;
import com.petrovoleh.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    public void testCreateOrder() throws Exception {
        Order order = new Order(1, new Date(), "John", 100, Arrays.asList("item1", "item2"));

        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .content(asJsonString(order))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateOrder() throws Exception {
        Order order = new Order(1, new Date(), "John", 100, Arrays.asList("item1", "item2"));

        when(orderService.updateOrder(eq(1), any(Order.class))).thenReturn(order);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/1")
                        .content(asJsonString(order))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteOrder() throws Exception {
        when(orderService.deleteOrder(1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testUploadOrders() throws Exception {
        List<Order> orders = Arrays.asList(new Order(1, new Date(), "John", 100, Arrays.asList("item1", "item2")));

        when(orderService.saveOrders(anyList())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/upload")
                        .content(asJsonString(orders))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPostReport() throws Exception {
        // Create a sample OrderListRequest
        OrderListRequest request = new OrderListRequest();
        request.setName("John");

        // Convert the request object to JSON
        String requestBody = new ObjectMapper().writeValueAsString(request);

        // Perform the POST request with the request body
        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/_report")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testPostList() throws Exception {
        OrderListRequest request = new OrderListRequest();
        request.setName("Client1");
        request.setPage(0);
        request.setSize(10);

        List<Order> orders = Arrays.asList(new Order(1, new Date(), "John", 100, Arrays.asList("item1", "item2")));

        Page<Order> ordersPage = new PageImpl<>(orders);
        when(orderService.getOrdersByName("Client1", 0, 10)).thenReturn(ordersPage);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/_list")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
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
