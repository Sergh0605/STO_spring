package org.itentika.edu.spuzakov.mvc.controllers;

import org.itentika.edu.spuzakov.mvc.config.JavaConfig;
import org.itentika.edu.spuzakov.mvc.dto.ExOrderStatusDto;
import org.itentika.edu.spuzakov.mvc.dto.IdDto;
import org.itentika.edu.spuzakov.mvc.dto.ItemsDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Client;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JavaConfig.class})
@WebAppConfiguration
public class OrderControllerFailureIntegrationTest extends StoTestBase {

    @Test
    public void givenNewOrderJSON_whenCreateOrderByUserWithoutAdminRole_then403() throws Exception {
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
                        .param("adminLogin", "user")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(newOrder)))
                .andExpect(status().is(403));
    }

    @Test
    public void givenNewOrderJSONWithNullClientName_whenCreateOrder_then403() throws Exception {
        Order newOrder = Order.builder()
                .reason("Test reason")
                .comment("Test comment")
                .client(Client.builder()
                        .name(null)
                        .phone("+71234567891")
                        .address("Test address")
                        .build())
                .build();
        this.mockMvc
                .perform(post("/api/order")
                        .param("adminLogin", "user")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(newOrder)))
                .andExpect(status().is(400));
    }

    @Test
    public void givenOrderIdAndUserWithoutAcceptRole_whenAcceptOrder_then403() throws Exception {
        Long orderId = getIdOfNewOrder();
        IdDto idDto = IdDto.builder()
                .id(6L)
                .build();

        this.mockMvc
                .perform(post("/api/order/{orderId}/accept", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(idDto)))
                .andExpect(status().is(403));
    }

    @Test
    public void givenOrderIdAndItemsWithWrongPriceItemId_whenAddItems_then404() throws Exception {
        Long orderId = getIdOfNewOrder();
        ItemsDto items = getItems();
        items.getItems().get(0).getPriceItem().setId(50L);

        this.mockMvc
                .perform(post("/api/order/{orderId}/items", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(items)))
                .andExpect(status().is(404));
    }

    @Test
    public void givenNewOrder_whenAddWrongStatus_then400() throws Exception {
        Long orderId = getIdOfNewOrder();
        ExOrderStatusDto status = ExOrderStatusDto.builder()
                .status("READY")
                .comment("Ready")
                .build();
        this.mockMvc
                .perform(post("/api/order/{orderId}/status", orderId)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(getJSON(status)))
                .andExpect(status().is(400));
    }
}
