package com.alexnosevichdev.orders.service;

import com.alexnosevichdev.orders.dto.OrderEvent;
import com.alexnosevichdev.orders.dto.OrderStatus;
import com.alexnosevichdev.orders.entity.Order;
import com.alexnosevichdev.orders.repository.OrderRepository;
import tools.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper = JsonMapper.builder()
            .build();


    public OrderEvent createOrder(Long userId, Long productId, int quantity, BigDecimal totalAmount) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.NEW);
        order.setTimestamp(LocalDateTime.now());

        Order newOrder = orderRepository.save(order);
        log.info("Заказ создан: orderId={}, userId={}", newOrder.getOrderId(), newOrder.getUserId());

        OrderEvent event = new OrderEvent();
        event.setOrderId(newOrder.getOrderId());
        event.setUserId(newOrder.getUserId());
        event.setProductId(newOrder.getProductId());
        event.setQuantity(newOrder.getQuantity());
        event.setTotalAmount(newOrder.getTotalAmount());
        event.setOrderStatus(newOrder.getOrderStatus());
        event.setTimestamp(newOrder.getTimestamp());

        try {
            kafkaTemplate.send("new_orders", event.getOrderId().toString(),
                    jsonMapper.writeValueAsString(event));
            log.info("Сообщение о заказе отправлено: orderId={}, topic=new_orders", event.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка отправки в кафка: orderId={}", event.getOrderId());
            throw new RuntimeException("Ошибка кафка", e);
        }
        return event;
    }

    public OrderEvent updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Заказ не найден {}", orderId);
                    return new RuntimeException("Заказ не найден: " + orderId);
                });

        existingOrder.setOrderStatus(orderStatus);
        existingOrder.setTimestamp(LocalDateTime.now());

        orderRepository.save(existingOrder);
        log.info("Заказ обновлен: {}{}", orderId, orderStatus);

        OrderEvent event = new OrderEvent();
        event.setOrderId(existingOrder.getOrderId());
        event.setProductId(existingOrder.getProductId());
        event.setUserId(existingOrder.getUserId());
        event.setQuantity(existingOrder.getQuantity());
        event.setTotalAmount(existingOrder.getTotalAmount());
        event.setOrderStatus(existingOrder.getOrderStatus());
        event.setTimestamp(existingOrder.getTimestamp());

        try {
            kafkaTemplate.send("updated_orders", event.getOrderId().toString(),
                    jsonMapper.writeValueAsString(event));
            log.info("Сообщение об обновлении заказа отправлено: orderId={}," +
                    "orderStatus={}, topic=updated_orders", event.getOrderId(), event.getOrderStatus());
        } catch (Exception e) {
            log.error("Ошибка отправки в кафка: orderId={}, orderStatus={}", event.getOrderId(),
                    event.getOrderStatus());
            throw new RuntimeException(e);
        }
        return event;
    }
}
