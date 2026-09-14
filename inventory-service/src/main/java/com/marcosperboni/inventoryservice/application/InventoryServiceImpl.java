package com.marcosperboni.inventoryservice.application;

import com.marcosperboni.inventoryservice.api.dto.InventoryItemRequest;
import com.marcosperboni.inventoryservice.api.dto.InventoryItemResponse;
import com.marcosperboni.inventoryservice.domain.InventoryItem;
import com.marcosperboni.inventoryservice.domain.InventoryItemNotFoundException;
import com.marcosperboni.inventoryservice.infrastructure.persistence.InventoryItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

	private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

	private final InventoryItemRepository inventoryItemRepository;

	public InventoryServiceImpl(InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}

	@Override
	@Transactional
	public InventoryItemResponse create(InventoryItemRequest request) {
		InventoryItem item = new InventoryItem(request.productName(), request.quantityAvailable());
		return InventoryItemResponse.from(inventoryItemRepository.save(item));
	}

	@Override
	public InventoryItemResponse findById(Long id) {
		return inventoryItemRepository.findById(id)
				.map(InventoryItemResponse::from)
				.orElseThrow(() -> new InventoryItemNotFoundException(id));
	}

	@Override
	public List<InventoryItemResponse> findAll() {
		return inventoryItemRepository.findAll().stream().map(InventoryItemResponse::from).toList();
	}

	@Override
	@Transactional
	public InventoryItemResponse update(Long id, InventoryItemRequest request) {
		InventoryItem item = inventoryItemRepository.findById(id)
				.orElseThrow(() -> new InventoryItemNotFoundException(id));
		item.setProductName(request.productName());
		item.setQuantityAvailable(request.quantityAvailable());
		return InventoryItemResponse.from(inventoryItemRepository.save(item));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!inventoryItemRepository.existsById(id)) {
			throw new InventoryItemNotFoundException(id);
		}
		inventoryItemRepository.deleteById(id);
	}

	@Override
	@Transactional
	public void reserveStock(String productName, int quantity) {
		inventoryItemRepository.findByProductName(productName).ifPresentOrElse(
				item -> {
					item.reserve(quantity);
					inventoryItemRepository.save(item);
				},
				() -> log.warn("Received order.created for unknown product '{}', skipping stock reservation", productName));
	}
}
