package com.marcosperboni.orderservice.api;

import com.marcosperboni.orderservice.api.controller.OrderController;
import com.marcosperboni.orderservice.api.dto.OrderRequest;
import com.marcosperboni.orderservice.api.dto.OrderResponse;
import com.marcosperboni.orderservice.application.OrderService;
import com.marcosperboni.orderservice.domain.OrderNotFoundException;
import com.marcosperboni.orderservice.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@Test
	void createShouldReturn201() throws Exception {
		OrderResponse response = new OrderResponse(1L, "Alice", "Keyboard", 2, OrderStatus.CREATED, Instant.now());
		when(orderService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/orders")
						.contentType("application/json")
						.content("""
								{"customerName":"Alice","productName":"Keyboard","quantity":2}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.productName").value("Keyboard"));
	}

	@Test
	void createShouldReturn400WhenInvalid() throws Exception {
		mockMvc.perform(post("/api/orders")
						.contentType("application/json")
						.content("""
								{"customerName":"","productName":"Keyboard","quantity":0}
								"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void findByIdShouldReturn404WhenMissing() throws Exception {
		when(orderService.findById(eq(99L))).thenThrow(new OrderNotFoundException(99L));

		mockMvc.perform(get("/api/orders/{id}", 99L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Order not found with id: 99"));
	}

	@Test
	void findAllShouldReturn200() throws Exception {
		OrderResponse response = new OrderResponse(1L, "Alice", "Keyboard", 2, OrderStatus.CREATED, Instant.now());
		when(orderService.findAll()).thenReturn(List.of(response));

		mockMvc.perform(get("/api/orders"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void deleteShouldReturn204() throws Exception {
		mockMvc.perform(delete("/api/orders/{id}", 1L))
				.andExpect(status().isNoContent());
	}
}
