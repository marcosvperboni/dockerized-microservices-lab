package com.marcosperboni.inventoryservice.api;

import com.marcosperboni.inventoryservice.api.controller.InventoryItemController;
import com.marcosperboni.inventoryservice.api.dto.InventoryItemResponse;
import com.marcosperboni.inventoryservice.application.InventoryService;
import com.marcosperboni.inventoryservice.domain.InventoryItemNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryItemController.class)
class InventoryItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private InventoryService inventoryService;

	@Test
	void createShouldReturn201() throws Exception {
		InventoryItemResponse response = new InventoryItemResponse(1L, "Keyboard", 10);
		when(inventoryService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/inventory")
						.contentType("application/json")
						.content("""
								{"productName":"Keyboard","quantityAvailable":10}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.productName").value("Keyboard"));
	}

	@Test
	void findByIdShouldReturn404WhenMissing() throws Exception {
		when(inventoryService.findById(eq(5L))).thenThrow(new InventoryItemNotFoundException(5L));

		mockMvc.perform(get("/api/inventory/{id}", 5L))
				.andExpect(status().isNotFound());
	}

	@Test
	void findAllShouldReturn200() throws Exception {
		when(inventoryService.findAll()).thenReturn(List.of(new InventoryItemResponse(1L, "Mouse", 5)));

		mockMvc.perform(get("/api/inventory"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].productName").value("Mouse"));
	}
}
