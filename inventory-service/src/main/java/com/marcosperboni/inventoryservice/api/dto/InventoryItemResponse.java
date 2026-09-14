package com.marcosperboni.inventoryservice.api.dto;

import com.marcosperboni.inventoryservice.domain.InventoryItem;

import java.io.Serializable;

public record InventoryItemResponse(
		Long id,
		String productName,
		Integer quantityAvailable
) implements Serializable {

	public static InventoryItemResponse from(InventoryItem item) {
		return new InventoryItemResponse(item.getId(), item.getProductName(), item.getQuantityAvailable());
	}
}
