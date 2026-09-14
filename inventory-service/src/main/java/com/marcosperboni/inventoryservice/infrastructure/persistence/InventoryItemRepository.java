package com.marcosperboni.inventoryservice.infrastructure.persistence;

import com.marcosperboni.inventoryservice.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

	Optional<InventoryItem> findByProductName(String productName);
}
