package com.projects.notificationService.config;

import com.projects.notificationService.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class DeliveryEventTopicConfig {
    @Bean
    public NewTopic deliveryEventsTopic(){
        return TopicBuilder
                .name(KafkaTopics.DELIVERY_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
