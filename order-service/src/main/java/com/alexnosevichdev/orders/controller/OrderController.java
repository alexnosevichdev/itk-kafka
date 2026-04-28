package com.alexnosevichdev.orders.controller;

import com.alexnosevichdev.orders.dto.OrderEvent;
import com.alexnosevichdev.orders.dto.OrderStatus;
import com.alexnosevichdev.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    public record CreateOrderRequest(Long userId,
                                     Long productId,
                                     int quantity,
                                     BigDecimal totalAmount) {
    }
    private final OrderService orderService;

    @PostMapping
    public OrderEvent createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request.userId(),
                request.productId(),
                request.quantity(),
                request.totalAmount());
    }

    @PatchMapping("/{orderId}/status")
    public OrderEvent updateOrder(@PathVariable UUID orderId, @RequestParam OrderStatus orderStatus) {
        return orderService.updateOrderStatus(orderId, orderStatus);
    }
}
