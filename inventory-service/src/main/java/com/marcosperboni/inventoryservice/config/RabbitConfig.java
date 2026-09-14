package com.marcosperboni.inventoryservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	public static final String ORDERS_EXCHANGE = "orders.exchange";
	public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
	public static final String INVENTORY_QUEUE = "inventory.order-created.queue";

	@Bean
	public TopicExchange ordersExchange() {
		return new TopicExchange(ORDERS_EXCHANGE, true, false);
	}

	@Bean
	public Queue inventoryQueue() {
		return new Queue(INVENTORY_QUEUE, true);
	}

	@Bean
	public Binding inventoryBinding(Queue inventoryQueue, TopicExchange ordersExchange) {
		return BindingBuilder.bind(inventoryQueue).to(ordersExchange).with(ORDER_CREATED_ROUTING_KEY);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
