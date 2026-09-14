package com.marcosperboni.inventoryservice.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryItemRequest(
		@NotBlank(message = "productName is required") String productName,
		@NotNull(message = "quantityAvailable is required") @Min(value = 0, message = "quantityAvailable cannot be negative") Integer quantityAvailable
) {
}
