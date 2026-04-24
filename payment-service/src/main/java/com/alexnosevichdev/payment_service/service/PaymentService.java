package com.alexnosevichdev.payment_service.service;

import com.alexnosevichdev.payment_service.dto.OrderEvent;
import com.alexnosevichdev.payment_service.dto.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @KafkaListener(
            topics = "new_orders", //кого слущать
            //имя слушателей задано в application.properties.yaml
            concurrency = "${kafka.partitions}" //сколько читать потоков
    )
    public void processPayment(String message) {
        try {
            OrderEvent event = jsonMapper.readValue(message, OrderEvent.class);
            log.info("Есть заказ для оплаты: orderId={}, totalAmount={}",
                    event.getOrderId(), event.getTotalAmount());

            //Типо провели оплату и меняем статус
            event.setOrderStatus(OrderStatus.PAID);
            kafkaTemplate.send("payed_orders",
                    event.getOrderId().toString(),
                    jsonMapper.writeValueAsString(event));

            log.info("Заказ успешно оплачен: order_id={}", event.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка при обработке оплаты: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
