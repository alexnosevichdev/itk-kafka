package com.alexnosevichdev.shipping_service.service;

import com.alexnosevichdev.shipping_service.dto.OrderEvent;
import com.alexnosevichdev.shipping_service.dto.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @KafkaListener(
            topics = "payed_orders",
            concurrency = "${kafka.partitions}"
    )
    public void processShipping(String message) {
        try {
            OrderEvent event = jsonMapper.readValue(message, OrderEvent.class);
            log.info("Можно установить статус Доставлен для заказа: orderId={}," +
                    "orderStatus={}", event.getOrderId(), event.getOrderStatus());

            //Допустим, заказ был доставлен
            event.setOrderStatus(OrderStatus.DELIVERED);
            kafkaTemplate.send("sent_orders",
                    event.getOrderId().toString(),
                    jsonMapper.writeValueAsString(event));

            log.info("Заказ доставлен: orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка смены статуса: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
