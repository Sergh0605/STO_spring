package org.itentika.edu.spuzakov.mvc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.itentika.edu.spuzakov.mvc.config.JavaConfig;
import org.itentika.edu.spuzakov.mvc.dto.*;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Client;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JavaConfig.class})
@WebAppConfiguration
public class OrderControllerIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DataSource dataSource;
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @AfterEach
    void dropData() throws Exception {
        executeSql("dropData.sql");
    }

    @Test
    public void givenNewOrderJSON_whenCreateOrder_thenVerifyResponse() throws Exception {
        Order newOrder = Order.builder()
                .reason("Test reason")
                .comment("Test comment")
                .client(Client.builder()
                        .name("Test Client name")
                        .phone("+71234567891")
                        .address("Test address")
                        .build())
                .build();
        this.mockMvc
                .perform(post("/api/order")
                        .param("adminName", "Администраторов")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(newOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value(newOrder.getReason()))
                .andExpect(jsonPath("$.client.name").value(newOrder.getClient().getName()))
                .andExpect(jsonPath("$.administrator.name").value("Администраторов"))
                .andExpect(jsonPath("$.orderHistory.length()").value(1));
    }

    @Test
    public void givenOrderIdAndMasterId_whenAcceptOrder_thenVerifyResponse() throws Exception {
        Long orderId = getIdOfNewOrder();
        IdDto idDto = IdDto.builder()
                .id(2L)
                .build();

        this.mockMvc
                .perform(post("/api/order/{orderId}/accept", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(idDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.master.id").value(idDto.getId()))
                .andExpect(jsonPath("$.orderHistory.length()").value(2));
    }

    @Test
    public void givenOrderIdAndItems_whenAddItems_thenVerifyResponse() throws Exception {
        Long orderId = getIdOfNewOrder();
        ItemsDto items = getItems();

        this.mockMvc
                .perform(post("/api/order/{orderId}/items", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(items)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderItem.length()").value(items.getItems().size()));
    }

    @Test
    public void givenNewOrder_whenAddStatus_thenVerifyResponse() throws Exception {
        Long orderId = getIdOfNewOrder();
        ExOrderStatusDto status = ExOrderStatusDto.builder()
                .status("DONE")
                .comment("Ready")
                .build();
        this.mockMvc
                .perform(post("/api/order/{orderId}/status", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(status)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderHistory.length()").value(2L));
    }

    @Test
    public void givenOrderWithDoneStatusA_whenGetCurrentStatus_thenVerify() throws Exception {
        Long orderId = getIdOfNewOrder();
        ExOrderStatusDto status = ExOrderStatusDto.builder()
                .status("DONE")
                .comment("Ready")
                .build();
        this.mockMvc
                .perform(post("/api/order/{orderId}/status", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(status)));
        this.mockMvc
                .perform(get("/api/order/{orderId}/status", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.comment").value("Ready"));


    }

    @Test
    public void givenNewOrder_whenChangeOrderFields_thenVerify() throws Exception {
        Long orderId = getIdOfNewOrder();
        Order changedOrder = Order.builder()
                .id(orderId)
                .reason("Changed Test reason")
                .comment("Changed comment")
                .client(Client.builder()
                        .name("Changed Test Client name")
                        .phone("+71234567899")
                        .address("Changed Test address")
                        .build())
                .build();
        this.mockMvc
                .perform(put("/api/order")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(changedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value(changedOrder.getReason()))
                .andExpect(jsonPath("$.comment").value(changedOrder.getComment()))
                .andExpect(jsonPath("$.client.name").value(changedOrder.getClient().getName()))
                .andExpect(jsonPath("$.client.phone").value(changedOrder.getClient().getPhone()))
                .andExpect(jsonPath("$.client.address").value(changedOrder.getClient().getAddress()));
    }

    private String getJSON(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(obj);
    }

    private Long getIdOfNewOrder() throws Exception {
        OrderDto newOrder = OrderDto.builder()
                .reason("Test reason")
                .comment("Test comment")
                .client(ClientDto.builder()
                        .name("Test Client name")
                        .phone("+71234567891")
                        .address("Test address")
                        .build())
                .build();
        MvcResult result = this.mockMvc.perform(post("/api/order")
                .param("adminName", "Администраторов")
                .contentType(APPLICATION_JSON_UTF8)
                .content(getJSON(newOrder))).andReturn();
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        OrderDto order = mapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        return order.getId();
    }

    private ItemsDto getItems() {
        List<OrderItemDto> orderItems = new ArrayList<>();
        orderItems.add(
                OrderItemDto.builder()
                        .quantity(2)
                        .cost(600L)
                        .priceItem(PriceItemDto.builder()
                                .id(8L)
                                .unit(UnitDto.builder()
                                        .id(1L)
                                        .build())
                                .build())
                        .build()
        );
        orderItems.add(
                OrderItemDto.builder()
                        .quantity(5)
                        .cost(1500L)
                        .priceItem(PriceItemDto.builder()
                                .id(2L)
                                .unit(UnitDto.builder()
                                        .id(1L)
                                        .build())
                                .build())
                        .build()
        );
        ItemsDto items = new ItemsDto();
        items.setItems(orderItems);
        return items;
    }

    private void executeSql(String fileName) throws Exception {
        Resource resource = new ClassPathResource(fileName);
        String initSql = new String(Files.readAllBytes(resource.getFile().toPath()));
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(initSql);
        }
    }
}
