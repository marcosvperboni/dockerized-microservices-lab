package com.marcosperboni.orderservice.api.controller;

import com.marcosperboni.orderservice.api.dto.OrderRequest;
import com.marcosperboni.orderservice.api.dto.OrderResponse;
import com.marcosperboni.orderservice.application.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
		OrderResponse created = orderService.create(request);
		return ResponseEntity.created(URI.create("/api/orders/" + created.id())).body(created);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> findById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(orderService.findById(id));
	}

	@GetMapping
	public ResponseEntity<List<OrderResponse>> findAll() {
		return ResponseEntity.ok(orderService.findAll());
	}

	@PutMapping("/{id}")
	public ResponseEntity<OrderResponse> update(@PathVariable("id") Long id, @Valid @RequestBody OrderRequest request) {
		return ResponseEntity.ok(orderService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		orderService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
