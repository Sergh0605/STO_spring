package org.itentika.edu.spuzakov.mvc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.itentika.edu.spuzakov.mvc.dto.*;
import org.itentika.edu.spuzakov.mvc.persistence.dao.OrderRepository;
import org.itentika.edu.spuzakov.mvc.persistence.dao.StaffRepository;
import org.itentika.edu.spuzakov.mvc.services.ClientService;
import org.itentika.edu.spuzakov.mvc.services.OrderStatusService;
import org.itentika.edu.spuzakov.mvc.services.StaffService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class StoTestBase {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DataSource dataSource;
    @Autowired
    protected ConversionService conversionService;
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected ClientService clientService;
    @Autowired
    protected StaffRepository staffRepository;
    @Autowired
    protected StaffService staffService;
    @Autowired
    protected OrderStatusService orderStatusService;
    @Value("#{'${sto.sec.rights.addOrder}'.split(',')}")
    protected List<String> addOrderRoles;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    protected MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @AfterEach
    void dropData() throws Exception {
        executeSql("dropData.sql");
    }

    protected String getJSON(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(obj);
    }

    protected OrderDto getOrderDto(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(json, OrderDto.class);
    }

    protected Long getIdOfNewOrder() throws Exception {
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
                .param("adminLogin", "admin")
                .contentType(APPLICATION_JSON_UTF8)
                .content(getJSON(newOrder))).andReturn();
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        OrderDto order = mapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);
        return order.getId();
    }

    protected ItemsDto getItems() {
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
            connection.commit();
        }
    }

    protected long countUniqueItems(List<OrderItemDto> currentItems, List<OrderItemDto> newItems) {
        List<OrderItemDto> allItems = new ArrayList<>(currentItems);
        allItems.addAll(newItems);
        return allItems.stream().map(i -> i.getPriceItem().getId()).distinct().count();
    }
}
