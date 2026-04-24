package com.alexnosevichdev.kafkahw.dto;

import com.alexnosevichdev.kafkahw.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private UUID orderId;
    private Long userId;
    private Long productId;
    private int quantity;
    private OrderStatus orderStatus;
    private BigDecimal totalPrice;
    private LocalDateTime timestamp;
}
