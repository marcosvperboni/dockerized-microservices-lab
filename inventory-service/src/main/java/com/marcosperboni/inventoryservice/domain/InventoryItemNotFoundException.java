package com.marcosperboni.inventoryservice.domain;

public class InventoryItemNotFoundException extends RuntimeException {

	public InventoryItemNotFoundException(Long id) {
		super("Inventory item not found with id: " + id);
	}
}
