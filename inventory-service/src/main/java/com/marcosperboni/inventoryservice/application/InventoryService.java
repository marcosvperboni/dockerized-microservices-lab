package com.marcosperboni.inventoryservice.application;

import com.marcosperboni.inventoryservice.api.dto.InventoryItemRequest;
import com.marcosperboni.inventoryservice.api.dto.InventoryItemResponse;

import java.util.List;

public interface InventoryService {

	InventoryItemResponse create(InventoryItemRequest request);

	InventoryItemResponse findById(Long id);

	List<InventoryItemResponse> findAll();

	InventoryItemResponse update(Long id, InventoryItemRequest request);

	void delete(Long id);

	void reserveStock(String productName, int quantity);
}
