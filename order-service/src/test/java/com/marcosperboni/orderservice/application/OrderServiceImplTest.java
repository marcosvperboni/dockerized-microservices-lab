package com.marcosperboni.orderservice.application;

import com.marcosperboni.orderservice.api.dto.OrderRequest;
import com.marcosperboni.orderservice.api.dto.OrderResponse;
import com.marcosperboni.orderservice.domain.Order;
import com.marcosperboni.orderservice.domain.OrderCreatedEvent;
import com.marcosperboni.orderservice.domain.OrderNotFoundException;
import com.marcosperboni.orderservice.infrastructure.messaging.OrderEventPublisher;
import com.marcosperboni.orderservice.infrastructure.persistence.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderEventPublisher orderEventPublisher;

	private OrderServiceImpl orderService;

	@BeforeEach
	void setUp() {
		orderService = new OrderServiceImpl(orderRepository, orderEventPublisher);
	}

	@Test
	void createShouldPersistOrderAndPublishEvent() {
		OrderRequest request = new OrderRequest("Alice", "Keyboard", 2);
		Order saved = new Order("Alice", "Keyboard", 2);
		setId(saved, 1L);
		when(orderRepository.save(any(Order.class))).thenReturn(saved);

		OrderResponse response = orderService.create(request);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.productName()).isEqualTo("Keyboard");

		ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
		verify(orderEventPublisher, times(1)).publishOrderCreated(captor.capture());
		assertThat(captor.getValue().orderId()).isEqualTo(1L);
		assertThat(captor.getValue().quantity()).isEqualTo(2);
	}

	@Test
	void findByIdShouldThrowWhenOrderMissing() {
		when(orderRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> orderService.findById(99L))
				.isInstanceOf(OrderNotFoundException.class)
				.hasMessageContaining("99");
	}

	@Test
	void findAllShouldMapAllOrders() {
		Order order = new Order("Bob", "Mouse", 1);
		setId(order, 5L);
		when(orderRepository.findAll()).thenReturn(List.of(order));

		List<OrderResponse> result = orderService.findAll();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).customerName()).isEqualTo("Bob");
	}

	@Test
	void deleteShouldThrowWhenOrderMissing() {
		when(orderRepository.existsById(42L)).thenReturn(false);

		assertThatThrownBy(() -> orderService.delete(42L)).isInstanceOf(OrderNotFoundException.class);
		verify(orderRepository, never()).deleteById(any());
	}

	private static void setId(Order order, Long id) {
		try {
			var field = Order.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(order, id);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
