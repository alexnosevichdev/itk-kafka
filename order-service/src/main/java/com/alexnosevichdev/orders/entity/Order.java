package com.alexnosevichdev.orders.entity;

import com.alexnosevichdev.orders.dto.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @NonNull
    private Long productId;

    @NonNull
    private Long userId;

    @NonNull
    private int quantity;

    @NonNull
    private BigDecimal totalAmount;

    @NonNull
    private OrderStatus orderStatus;
    private LocalDateTime timestamp;

}
