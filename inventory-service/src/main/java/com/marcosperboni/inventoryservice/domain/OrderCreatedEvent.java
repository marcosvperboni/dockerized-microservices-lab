package com.marcosperboni.inventoryservice.domain;

import java.io.Serializable;

public record OrderCreatedEvent(
		Long orderId,
		String productName,
		Integer quantity,
		String customerName
) implements Serializable {
}
