package org.itentika.edu.spuzakov.mvc.controllers;

import org.itentika.edu.spuzakov.mvc.config.JavaConfig;
import org.itentika.edu.spuzakov.mvc.dto.ExOrderStatusDto;
import org.itentika.edu.spuzakov.mvc.dto.IdDto;
import org.itentika.edu.spuzakov.mvc.dto.ItemsDto;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Client;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JavaConfig.class})
@WebAppConfiguration
public class OrderControllerIntegrationTest extends StoTestBase {
    @Test
    public void givenNewOrder_whenCreateOrder_thenVerifyResponse() throws Exception {
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
                        .param("adminLogin", "admin")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(newOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value(newOrder.getReason()))
                .andExpect(jsonPath("$.client.name").value(newOrder.getClient().getName()))
                .andExpect(jsonPath("$.administrator.name").value("Администраторов"))
                .andExpect(jsonPath("$.orderHistory.length()").value(1));
    }

    @Test
    public void givenDuplicatedOrder_whenCreateOrder_thenVerifyResponse() throws Exception {
        OrderDto oldOrder = createNewOrder();
        OrderDto duplicatedOrder = OrderDto.builder()
                .reason(oldOrder.getReason())
                .comment("New comment")
                .client(oldOrder.getClient())
                .build();

        this.mockMvc
                .perform(post("/api/order")
                        .param("adminLogin", "admin")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(duplicatedOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(oldOrder.getId()))
                .andExpect(jsonPath("$.reason").value(duplicatedOrder.getReason()))
                .andExpect(jsonPath("$.client.id").value(oldOrder.getClient().getId()))
                .andExpect(jsonPath("$.administrator.name").value("Администраторов"))
                .andExpect(jsonPath("$.orderHistory.length()").value(1));
    }

    @Test
    public void givenOrderIdAndMasterId_whenAcceptOrder_thenVerifyResponse() throws Exception {
        Long orderId = createNewOrder().getId();
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
    public void givenOrderIdAndItems_whenAddItemsToEmptyOrder_thenVerifyResponse() throws Exception {
        Long orderId = createNewOrder().getId();
        ItemsDto items = getItems();

        this.mockMvc
                .perform(post("/api/order/{orderId}/items", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(items)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderItem.length()").value(items.getItems().size()));
    }

    @Test
    public void givenOrderIdAndItems_whenAddDoubledItemsToEmptyOrder_thenVerifyResponse() throws Exception {
        Long orderId = createNewOrder().getId();
        ItemsDto items = getItems();
        items.getItems().get(0).getPriceItem().setId(2L);
        long itemsCount = countUniqueItems(items.getItems(), Collections.emptyList());
        this.mockMvc
                .perform(post("/api/order/{orderId}/items", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(items)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderItem.length()").value(itemsCount));
    }

    @Test
    public void givenOrderIdAndItems_whenAddItemsToOrderWithOtherItems_thenVerifyResponse() throws Exception {
        Long orderId = createNewOrder().getId();
        ItemsDto oldItems = getItems();
        ItemsDto newItems = getItems();
        newItems.getItems().get(0).getPriceItem().setId(7L);
        long itemsCount = countUniqueItems(oldItems.getItems(), newItems.getItems());
        this.mockMvc
                .perform(post("/api/order/{orderId}/items", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(oldItems)));

        this.mockMvc
                .perform(post("/api/order/{orderId}/items", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(newItems)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderItem.length()").value(itemsCount));
    }

    @Test
    public void givenNewOrder_whenAddStatus_thenVerifyResponse() throws Exception {
        Long orderId = createNewOrder().getId();
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
        Long orderId = createNewOrder().getId();
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
        Long orderId = createNewOrder().getId();
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
}
