package com.alexnosevichdev.notification_service.service;

import com.alexnosevichdev.notification_service.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @KafkaListener(
            topics = "sent_orders",
            concurrency = "${kafka.partitions}"
    )
    public void processNotification(String message) {
        try {
            OrderEvent event = jsonMapper.readValue(message, OrderEvent.class);

            log.info("Уведомление для пользователя userId={}: "
            + "Ваш заказ orderId={} доставлен: "
            + "Статус orderStatus={}:", event.getUserId(),
                    event.getOrderId(),
                    event.getOrderStatus());
        } catch (Exception e) {
            log.error("Сбой отправки уведомления: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
