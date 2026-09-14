package com.marcosperboni.orderservice.infrastructure.persistence;

import com.marcosperboni.orderservice.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
