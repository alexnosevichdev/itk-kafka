package com.alexnosevichdev.shipping_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {
    private static final long RETRY_INTERVAL = 1000L; //ms
    private static final long RETRY_ATTEMPTS = 3L; //попытки

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {

        //куда пошлем мертые сообщения (надо добавить .DLT)
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, e) ->
                {
                    log.error("Сообщение отправлено в DLT" +
                                    "Топик: {}, Ключ: {}, Ошибка: {}",
                            record.topic() + ".DLT",
                            record.key(),
                            e.getMessage()
                    );

                    return new org.apache.kafka.common.TopicPartition(
                            record.topic()+".DLT", 0);
                }

        );
        FixedBackOff backOff = new FixedBackOff(RETRY_INTERVAL, RETRY_ATTEMPTS);
        return new DefaultErrorHandler(recoverer, backOff);
    }

}

