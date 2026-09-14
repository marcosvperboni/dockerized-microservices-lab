package com.marcosperboni.inventoryservice.infrastructure.messaging;

import com.marcosperboni.inventoryservice.application.InventoryService;
import com.marcosperboni.inventoryservice.config.RabbitConfig;
import com.marcosperboni.inventoryservice.domain.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

	private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

	private final InventoryService inventoryService;

	public OrderCreatedListener(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@RabbitListener(queues = RabbitConfig.INVENTORY_QUEUE)
	public void onOrderCreated(OrderCreatedEvent event) {
		log.info("Received order.created event for orderId={}, product={}, quantity={}",
				event.orderId(), event.productName(), event.quantity());
		inventoryService.reserveStock(event.productName(), event.quantity());
	}
}
