package com.marcosperboni.inventoryservice.api.controller;

import com.marcosperboni.inventoryservice.api.dto.InventoryItemRequest;
import com.marcosperboni.inventoryservice.api.dto.InventoryItemResponse;
import com.marcosperboni.inventoryservice.application.InventoryService;
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
@RequestMapping("/api/inventory")
public class InventoryItemController {

	private final InventoryService inventoryService;

	public InventoryItemController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@PostMapping
	public ResponseEntity<InventoryItemResponse> create(@Valid @RequestBody InventoryItemRequest request) {
		InventoryItemResponse created = inventoryService.create(request);
		return ResponseEntity.created(URI.create("/api/inventory/" + created.id())).body(created);
	}

	@GetMapping("/{id}")
	public ResponseEntity<InventoryItemResponse> findById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(inventoryService.findById(id));
	}

	@GetMapping
	public ResponseEntity<List<InventoryItemResponse>> findAll() {
		return ResponseEntity.ok(inventoryService.findAll());
	}

	@PutMapping("/{id}")
	public ResponseEntity<InventoryItemResponse> update(@PathVariable("id") Long id,
			@Valid @RequestBody InventoryItemRequest request) {
		return ResponseEntity.ok(inventoryService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		inventoryService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
