package com.alexnosevichdev.orders.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    //обращение к свойствам из конфига
    @Value("${kafka.partitions:3}")
    private int partitions;

    @Bean
    public NewTopic newOrdersTopic() {
        return TopicBuilder
                .name("new_orders")
                .partitions(partitions)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic payedOrdersTopic() {
        return TopicBuilder
                .name("payed_orders")
                .partitions(partitions)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sentOrdersTopic() {
        return TopicBuilder
                .name("sent_orders")
                .partitions(partitions)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic updatedOrdersTopic() {
        return TopicBuilder
                .name("updated_orders")
                .partitions(partitions)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic shippedOrdersTopic() {
        return TopicBuilder
                .name("sent_orders")
                .partitions(partitions)
                .replicas(1)
                .build();
    }

}
