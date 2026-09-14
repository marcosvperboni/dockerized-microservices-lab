package com.marcosperboni.orderservice.application;

import com.marcosperboni.orderservice.api.dto.OrderRequest;
import com.marcosperboni.orderservice.api.dto.OrderResponse;
import com.marcosperboni.orderservice.config.CacheConfig;
import com.marcosperboni.orderservice.domain.Order;
import com.marcosperboni.orderservice.domain.OrderCreatedEvent;
import com.marcosperboni.orderservice.domain.OrderNotFoundException;
import com.marcosperboni.orderservice.infrastructure.messaging.OrderEventPublisher;
import com.marcosperboni.orderservice.infrastructure.persistence.OrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderEventPublisher orderEventPublisher;

	public OrderServiceImpl(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher) {
		this.orderRepository = orderRepository;
		this.orderEventPublisher = orderEventPublisher;
	}

	@Override
	@Transactional
	public OrderResponse create(OrderRequest request) {
		Order order = new Order(request.customerName(), request.productName(), request.quantity());
		Order saved = orderRepository.save(order);

		orderEventPublisher.publishOrderCreated(new OrderCreatedEvent(
				saved.getId(), saved.getProductName(), saved.getQuantity(), saved.getCustomerName()));

		return OrderResponse.from(saved);
	}

	@Override
	@Cacheable(cacheNames = CacheConfig.ORDERS_CACHE, key = "#id")
	public OrderResponse findById(Long id) {
		return orderRepository.findById(id)
				.map(OrderResponse::from)
				.orElseThrow(() -> new OrderNotFoundException(id));
	}

	@Override
	public List<OrderResponse> findAll() {
		return orderRepository.findAll().stream().map(OrderResponse::from).toList();
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = CacheConfig.ORDERS_CACHE, key = "#id")
	public OrderResponse update(Long id, OrderRequest request) {
		Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		order.setCustomerName(request.customerName());
		order.setProductName(request.productName());
		order.setQuantity(request.quantity());
		return OrderResponse.from(orderRepository.save(order));
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = CacheConfig.ORDERS_CACHE, key = "#id")
	public void delete(Long id) {
		if (!orderRepository.existsById(id)) {
			throw new OrderNotFoundException(id);
		}
		orderRepository.deleteById(id);
	}
}
