package com.marcosperboni.inventoryservice.application;

import com.marcosperboni.inventoryservice.api.dto.InventoryItemRequest;
import com.marcosperboni.inventoryservice.api.dto.InventoryItemResponse;
import com.marcosperboni.inventoryservice.domain.InventoryItem;
import com.marcosperboni.inventoryservice.domain.InventoryItemNotFoundException;
import com.marcosperboni.inventoryservice.infrastructure.persistence.InventoryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	private InventoryServiceImpl inventoryService;

	@BeforeEach
	void setUp() {
		inventoryService = new InventoryServiceImpl(inventoryItemRepository);
	}

	@Test
	void createShouldPersistItem() {
		InventoryItemRequest request = new InventoryItemRequest("Keyboard", 10);
		InventoryItem saved = new InventoryItem("Keyboard", 10);
		when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(saved);

		InventoryItemResponse response = inventoryService.create(request);

		assertThat(response.productName()).isEqualTo("Keyboard");
		assertThat(response.quantityAvailable()).isEqualTo(10);
	}

	@Test
	void findByIdShouldThrowWhenMissing() {
		when(inventoryItemRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> inventoryService.findById(1L))
				.isInstanceOf(InventoryItemNotFoundException.class);
	}

	@Test
	void reserveStockShouldDecrementQuantityWhenProductExists() {
		InventoryItem item = new InventoryItem("Mouse", 20);
		when(inventoryItemRepository.findByProductName("Mouse")).thenReturn(Optional.of(item));

		inventoryService.reserveStock("Mouse", 5);

		ArgumentCaptor<InventoryItem> captor = ArgumentCaptor.forClass(InventoryItem.class);
		verify(inventoryItemRepository).save(captor.capture());
		assertThat(captor.getValue().getQuantityAvailable()).isEqualTo(15);
	}

	@Test
	void reserveStockShouldNotFailWhenProductUnknown() {
		when(inventoryItemRepository.findByProductName("Unknown")).thenReturn(Optional.empty());

		inventoryService.reserveStock("Unknown", 5);

		verify(inventoryItemRepository, org.mockito.Mockito.never()).save(any());
	}

	@Test
	void reserveStockShouldNotGoBelowZero() {
		InventoryItem item = new InventoryItem("Monitor", 3);
		when(inventoryItemRepository.findByProductName("Monitor")).thenReturn(Optional.of(item));

		inventoryService.reserveStock("Monitor", 10);

		ArgumentCaptor<InventoryItem> captor = ArgumentCaptor.forClass(InventoryItem.class);
		verify(inventoryItemRepository).save(captor.capture());
		assertThat(captor.getValue().getQuantityAvailable()).isZero();
	}
}
