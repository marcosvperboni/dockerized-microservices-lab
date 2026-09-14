package com.marcosperboni.orderservice.application;

import com.marcosperboni.orderservice.api.dto.OrderRequest;
import com.marcosperboni.orderservice.api.dto.OrderResponse;

import java.util.List;

public interface OrderService {

	OrderResponse create(OrderRequest request);

	OrderResponse findById(Long id);

	List<OrderResponse> findAll();

	OrderResponse update(Long id, OrderRequest request);

	void delete(Long id);
}
