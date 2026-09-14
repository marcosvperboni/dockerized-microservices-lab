package com.marcosperboni.orderservice.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
		@NotBlank(message = "customerName is required") String customerName,
		@NotBlank(message = "productName is required") String productName,
		@NotNull(message = "quantity is required") @Min(value = 1, message = "quantity must be at least 1") Integer quantity
) {
}
