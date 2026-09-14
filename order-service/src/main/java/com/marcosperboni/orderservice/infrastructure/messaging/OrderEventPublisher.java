package com.marcosperboni.orderservice.infrastructure.messaging;

import com.marcosperboni.orderservice.config.RabbitConfig;
import com.marcosperboni.orderservice.domain.OrderCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

	private final RabbitTemplate rabbitTemplate;

	public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publishOrderCreated(OrderCreatedEvent event) {
		rabbitTemplate.convertAndSend(
				RabbitConfig.ORDERS_EXCHANGE,
				RabbitConfig.ORDER_CREATED_ROUTING_KEY,
				event);
	}
}
