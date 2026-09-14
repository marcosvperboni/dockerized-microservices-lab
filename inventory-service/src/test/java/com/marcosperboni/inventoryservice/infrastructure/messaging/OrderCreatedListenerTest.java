package com.marcosperboni.inventoryservice.infrastructure.messaging;

import com.marcosperboni.inventoryservice.application.InventoryService;
import com.marcosperboni.inventoryservice.domain.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderCreatedListenerTest {

	@Mock
	private InventoryService inventoryService;

	@Test
	void onOrderCreatedShouldDelegateToReserveStock() {
		OrderCreatedListener listener = new OrderCreatedListener(inventoryService);
		OrderCreatedEvent event = new OrderCreatedEvent(1L, "Keyboard", 3, "Alice");

		listener.onOrderCreated(event);

		verify(inventoryService).reserveStock("Keyboard", 3);
	}
}
