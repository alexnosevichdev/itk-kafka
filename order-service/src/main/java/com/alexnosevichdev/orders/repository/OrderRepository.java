package com.alexnosevichdev.orders.repository;

import com.alexnosevichdev.orders.dto.OrderStatus;
import com.alexnosevichdev.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(Long userId);
    List<Order> findByOrderStatus(OrderStatus orderStatus);
}
