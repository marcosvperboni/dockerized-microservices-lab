package com.marcosperboni.orderservice.api.dto;

import com.marcosperboni.orderservice.domain.Order;
import com.marcosperboni.orderservice.domain.OrderStatus;

import java.io.Serializable;
import java.time.Instant;

public record OrderResponse(
		Long id,
		String customerName,
		String productName,
		Integer quantity,
		OrderStatus status,
		Instant createdAt
) implements Serializable {

	public static OrderResponse from(Order order) {
		return new OrderResponse(
				order.getId(),
				order.getCustomerName(),
				order.getProductName(),
				order.getQuantity(),
				order.getStatus(),
				order.getCreatedAt());
	}
}
